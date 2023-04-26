/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken;

import static java.util.logging.Level.WARNING;
import static net.atos.client.zgw.shared.ZGWApiService.FIRST_PAGE_NUMBER_ZGW_APIS;
import static net.atos.client.zgw.shared.model.Results.NUM_ITEMS_PER_PAGE;
import static net.atos.zac.util.UriUtil.uuidFromURI;
import static net.atos.zac.zoeken.model.index.IndexStatus.ADD;
import static net.atos.zac.zoeken.model.index.IndexStatus.REMOVE;
import static net.atos.zac.zoeken.model.index.IndexStatus.UPDATE;
import static net.atos.zac.zoeken.model.index.ZoekObjectType.DOCUMENT;
import static net.atos.zac.zoeken.model.index.ZoekObjectType.TAAK;
import static net.atos.zac.zoeken.model.index.ZoekObjectType.ZAAK;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.CursorMarkParams;
import org.eclipse.microprofile.config.ConfigProvider;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;

import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.AbstractEnkelvoudigInformatieobject;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectListParameters;
import net.atos.client.zgw.shared.model.Results;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.ZaakListParameters;
import net.atos.zac.app.taken.model.TaakSortering;
import net.atos.zac.flowable.TakenService;
import net.atos.zac.shared.model.SorteerRichting;
import net.atos.zac.zoeken.converter.AbstractZoekObjectConverter;
import net.atos.zac.zoeken.model.ZoekObject;
import net.atos.zac.zoeken.model.index.HerindexerenInfo;
import net.atos.zac.zoeken.model.index.ZoekIndexEntity;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

@Singleton
@Transactional
public class IndexeerService {

    public static final String SOLR_CORE = "zac";

    private static final Logger LOG = Logger.getLogger(IndexeerService.class.getName());

    private static final int SOLR_MAX_RESULT = 100;

    private static final int TAKEN_MAX_RESULTS = 50;

    @Inject
    @Any
    private Instance<AbstractZoekObjectConverter<? extends ZoekObject>> converterInstances;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private DRCClientService drcClientService;

    @Inject
    private TakenService takenService;

    @Inject
    private IndexeerServiceHelper helper;

    private SolrClient solrClient;

    private final Set<ZoekObjectType> herindexerenBezig = new HashSet<>();

    public record Resultaat(long indexed, long removed, long remaining) {
        public Resultaat() {
            this(0, 0, 0);
        }
    }

    public IndexeerService() {
        final String solrUrl = ConfigProvider.getConfig().getValue("solr.url", String.class);
        solrClient = new Http2SolrClient.Builder(String.format("%s/solr/%s", solrUrl, SOLR_CORE)).build();
    }

    private void log(final ZoekObjectType objectType, final String message) {
        LOG.info("[%s] %s".formatted(objectType.toString(), message));
    }

    public void indexeerDirect(final String objectId, final ZoekObjectType objectType) {
        addToSolrIndex(Stream.of(getConverter(objectType).convert(objectId)));
    }

    public void indexeerDirect(final List<String> objectIds, final ZoekObjectType objectType) {
        addToSolrIndex(objectIds.stream().map(objectId -> getConverter(objectType).convert(objectId)));
    }

    @Transactional(Transactional.TxType.NEVER)
    public Resultaat indexeer(final int batchGrootte, final ZoekObjectType objectType) {
        if (herindexerenBezig.contains(objectType)) {
            log(objectType, "Indexeren niet gestart, markeren voor herindexeren is nog bezig");
            return new Resultaat();
        }
        final long count = helper.countMarkedObjects(objectType);
        if (count == 0) {
            return new Resultaat();
        }
        final List<ZoekIndexEntity> entities = helper.retrieveMarkedObjects(objectType, batchGrootte);
        log(objectType, "Indexeren gestart");
        log(objectType, "te indexeren: %d / %d".formatted(entities.size(), count));
        final AbstractZoekObjectConverter<? extends ZoekObject> converter = getConverter(objectType);
        final long added = addToSolrIndex(
                entities.stream()
                        .filter(zoekIndexEntity -> zoekIndexEntity.getStatus() == ADD || zoekIndexEntity.getStatus() == UPDATE)
                        .map(zoekIndexEntity -> convertToZoekObject(zoekIndexEntity, converter)));
        final long removed = removeFromSolrIndex(
                entities.stream()
                        .filter(zoekIndexEntity -> zoekIndexEntity.getStatus() == REMOVE)
                        .map(ZoekIndexEntity::getObjectId));
        final var resultaat = new Resultaat(added, removed, count - entities.size());
        log(objectType, "Indexeren gestopt");
        log(objectType, "geindexeerd: %d, verwijderd: %d, resterend: %d"
                .formatted(resultaat.indexed(), resultaat.removed(), resultaat.remaining()));
        return resultaat;
    }

    private AbstractZoekObjectConverter<? extends ZoekObject> getConverter(ZoekObjectType objectType) {
        for (AbstractZoekObjectConverter<? extends ZoekObject> converter : converterInstances) {
            if (converter.supports(objectType)) {
                return converter;
            }
        }
        throw new RuntimeException("[%s] No converter found".formatted(objectType.toString()));
    }

    private ZoekObject convertToZoekObject(final ZoekIndexEntity zoekIndexEntity,
            final AbstractZoekObjectConverter<? extends ZoekObject> converter) {
        ZoekObject zoekObject = null;
        try {
            zoekObject = converter.convert(zoekIndexEntity.getObjectId());
        } catch (final RuntimeException e) {
            LOG.log(WARNING, "[%s] '%s': %s".formatted(zoekIndexEntity.getType(), zoekIndexEntity.getObjectId(),
                                                       e.getMessage()));
        }
        if (zoekObject == null) {
            helper.removeMark(zoekIndexEntity.getObjectId());
        }
        return zoekObject;
    }

    private long addToSolrIndex(final Stream<ZoekObject> zoekObjecten) {
        final List<ZoekObject> beansToBeAdded = zoekObjecten
                .filter(Objects::nonNull)
                .toList();
        if (CollectionUtils.isNotEmpty(beansToBeAdded)) {
            try {
                solrClient.addBeans(beansToBeAdded);
                solrClient.commit();
            } catch (final IOException | SolrServerException e) {
                throw new RuntimeException(e);
            }
            helper.removeMarks(beansToBeAdded.stream().map(ZoekObject::getObjectId));
        }
        return beansToBeAdded.size();
    }

    private long removeFromSolrIndex(final Stream<String> ids) {
        final List<String> idsToBeDeleted = ids.toList();
        if (CollectionUtils.isNotEmpty(idsToBeDeleted)) {
            try {
                solrClient.deleteById(idsToBeDeleted);
                solrClient.commit();
            } catch (final IOException | SolrServerException e) {
                throw new RuntimeException(e);
            }
            helper.removeMarks(idsToBeDeleted.stream());
        }
        return idsToBeDeleted.size();
    }

    @Transactional(Transactional.TxType.NEVER)
    public HerindexerenInfo herindexeren(final ZoekObjectType objectType) {
        if (herindexerenBezig.contains(objectType)) {
            log(objectType, "Markeren voor herindexeren niet gestart, is nog bezig");
            return new HerindexerenInfo(0, 0, 0);
        }
        herindexerenBezig.add(objectType);
        try {
            log(objectType, "Markeren voor herindexeren gestart...");
            helper.removeMarks(objectType);
            markSolrEntitiesForRemoval(objectType);
            switch (objectType) {
                case ZAAK -> markAllZakenForReindexing();
                case DOCUMENT -> markAllInformatieobjectenForReindexing();
                case TAAK -> markAllTakenForReindexing();
            }
            final long removeCount = helper.countMarkedObjects(objectType, REMOVE);
            final long addCount = helper.countMarkedObjects(objectType, ADD);
            final long updateCount = helper.countMarkedObjects(objectType, UPDATE);
            log(objectType, "Markeren voor herindexeren gestopt");
            log(objectType, "toe te voegen: %d, bij te werken: %d, te verwijderen: %d"
                    .formatted(addCount, updateCount, removeCount));
            return new HerindexerenInfo(addCount, updateCount, removeCount);
        } finally {
            herindexerenBezig.remove(objectType);
        }
    }

    private void logProgress(final ZoekObjectType objectType, final long voortgang, final long grootte) {
        log(objectType, "gemarkeerd: %d / %d".formatted(voortgang, grootte));
    }

    private void markSolrEntitiesForRemoval(final ZoekObjectType objectType) {
        final SolrQuery query = new SolrQuery("*:*");
        query.setFields("id");
        query.addFilterQuery("type:%s".formatted(objectType.toString()));
        query.addSort("id", SolrQuery.ORDER.asc);
        query.setRows(SOLR_MAX_RESULT);
        String cursorMark = CursorMarkParams.CURSOR_MARK_START;
        boolean done = false;
        while (!done) {
            query.set(CursorMarkParams.CURSOR_MARK_PARAM, cursorMark);
            final QueryResponse response;
            try {
                response = solrClient.query(query);
            } catch (final SolrServerException | IOException e) {
                throw new RuntimeException(e);
            }
            helper.markObjectsForRemoval(
                    response.getResults().stream()
                            .map(document -> document.get("id"))
                            .filter(Objects::nonNull)
                            .map(Object::toString), objectType);
            final String nextCursorMark = response.getNextCursorMark();
            if (cursorMark.equals(nextCursorMark)) {
                done = true;
            } else {
                cursorMark = nextCursorMark;
            }
        }
    }

    private void markAllZakenForReindexing() {
        final ZaakListParameters listParameters = new ZaakListParameters();
        listParameters.setOrdering("-identificatie");
        listParameters.setPage(FIRST_PAGE_NUMBER_ZGW_APIS);
        boolean hasMore;
        do {
            hasMore = markZakenForReindexing(listParameters);
            listParameters.setPage(listParameters.getPage() + 1);
        } while (hasMore);
    }

    private void markAllInformatieobjectenForReindexing() {
        final EnkelvoudigInformatieobjectListParameters listParameters = new EnkelvoudigInformatieobjectListParameters();
        listParameters.setPage(FIRST_PAGE_NUMBER_ZGW_APIS);
        boolean hasMore;
        do {
            hasMore = markInformatieobjectenForReindexing(listParameters);
            listParameters.setPage(listParameters.getPage() + 1);
        } while (hasMore);
    }

    private void markAllTakenForReindexing() {
        final long numberOfTasks = takenService.countOpenTasks();
        int page = 0;
        boolean hasMore;
        do {
            hasMore = markTakenForReindexing(page, numberOfTasks);
            page++;
        } while (hasMore);
    }

    private boolean markZakenForReindexing(final ZaakListParameters listParameters) {
        final Results<Zaak> results = zrcClientService.listZaken(listParameters);
        helper.markObjectsForReindexing(results.getResults().stream()
                                                .map(Zaak::getUuid)
                                                .map(UUID::toString), ZAAK);
        logProgress(ZAAK,
                    (listParameters.getPage() - FIRST_PAGE_NUMBER_ZGW_APIS) * NUM_ITEMS_PER_PAGE + results.getResults()
                            .size(), results.getCount());
        return results.getNext() != null;
    }

    private boolean markInformatieobjectenForReindexing(
            final EnkelvoudigInformatieobjectListParameters listParameters) {
        final Results<EnkelvoudigInformatieobject> results = drcClientService.listEnkelvoudigInformatieObjecten(
                listParameters);
        helper.markObjectsForReindexing(results.getResults().stream()
                                                .map(AbstractEnkelvoudigInformatieobject::getUUID)
                                                .map(UUID::toString), DOCUMENT);
        logProgress(DOCUMENT,
                    (listParameters.getPage() - FIRST_PAGE_NUMBER_ZGW_APIS) * NUM_ITEMS_PER_PAGE + results.getResults()
                            .size(), results.getCount());
        return results.getNext() != null;
    }

    private boolean markTakenForReindexing(final int page, final long numberOfTasks) {
        final int firstResult = page * TAKEN_MAX_RESULTS;
        final List<Task> tasks = takenService.listOpenTasks(TaakSortering.CREATIEDATUM, SorteerRichting.DESCENDING,
                                                            firstResult, TAKEN_MAX_RESULTS);
        helper.markObjectsForReindexing(tasks.stream().map(TaskInfo::getId), TAAK);
        if (!tasks.isEmpty()) {
            logProgress(TAAK, firstResult + tasks.size(), numberOfTasks);
            return tasks.size() == TAKEN_MAX_RESULTS;
        }
        return false;
    }

    public void addOrUpdateZaak(final UUID zaakUUID, boolean inclusiefTaken) {
        helper.markObjectForIndexing(zaakUUID.toString(), ZAAK);
        if (inclusiefTaken) {
            takenService.listTasksForZaak(zaakUUID).stream()
                    .map(TaskInfo::getId)
                    .forEach(this::addOrUpdateTaak);
        }
    }

    public void addOrUpdateInformatieobject(final UUID informatieobjectUUID) {
        helper.markObjectForIndexing(informatieobjectUUID.toString(), DOCUMENT);
    }

    public void addOrUpdateInformatieobjectByZaakinformatieobject(final UUID zaakinformatieobjectUUID) {
        addOrUpdateInformatieobject(uuidFromURI(
                zrcClientService.readZaakinformatieobject(zaakinformatieobjectUUID).getInformatieobject()));
    }

    public void addOrUpdateTaak(final String taskID) {
        helper.markObjectForIndexing(taskID, TAAK);
    }

    public void removeZaak(final UUID zaakUUID) {
        helper.markObjectForRemoval(zaakUUID.toString(), ZAAK);
    }

    public void removeInformatieobject(final UUID informatieobjectUUID) {
        helper.markObjectForRemoval(informatieobjectUUID.toString(), DOCUMENT);
    }

    public void removeTaak(final String taskID) {
        helper.markObjectForRemoval(taskID, TAAK);
    }
}
