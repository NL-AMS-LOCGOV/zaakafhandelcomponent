/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken;

import static java.util.logging.Level.WARNING;
import static net.atos.client.zgw.shared.ZGWApiService.FIRST_PAGE_NUMBER_ZGW_APIS;
import static net.atos.client.zgw.shared.model.Results.NUM_ITEMS_PER_PAGE;
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
import java.util.stream.Collectors;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.CursorMarkParams;
import org.eclipse.microprofile.config.ConfigProvider;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;

import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectListParameters;
import net.atos.client.zgw.shared.model.Results;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;
import net.atos.client.zgw.zrc.model.ZaakListParameters;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.zac.app.taken.model.TaakSortering;
import net.atos.zac.flowable.TakenService;
import net.atos.zac.shared.model.SorteerRichting;
import net.atos.zac.util.UriUtil;
import net.atos.zac.zoeken.converter.AbstractZoekObjectConverter;
import net.atos.zac.zoeken.model.ZoekObject;
import net.atos.zac.zoeken.model.index.HerindexerenInfo;
import net.atos.zac.zoeken.model.index.IndexStatus;
import net.atos.zac.zoeken.model.index.ZoekIndexEntity;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

@Singleton
@Transactional
public class IndexeerService {

    public static final String SOLR_CORE = "zac";

    private static final Logger LOG = Logger.getLogger(IndexeerService.class.getName());

    private static int SOLR_MAX_RESULT = 100;

    private static int TAKEN_MAX_RESULTS = 50;

    @Inject
    @Any
    private Instance<AbstractZoekObjectConverter<? extends ZoekObject>> converterInstances;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private DRCClientService drcClientService;

    @Inject
    private TakenService takenService;

    @Inject
    private IndexeerServiceHelper helper;

    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;

    private SolrClient solrClient;

    private final Set<ZoekObjectType> herindexerenBezig = new HashSet<>();

    public record Resultaat(int indexed, int removed, int errors, int remaining) {
        public Resultaat() {
            this(0, 0, 0, 0);
        }
    }

    public IndexeerService() {
        final String solrUrl = ConfigProvider.getConfig().getValue("solr.url", String.class);
        solrClient = new Http2SolrClient.Builder(String.format("%s/solr/%s", solrUrl, SOLR_CORE)).build();
    }

    public void indexeerDirect(final String id, final ZoekObjectType type) {
        commitToSolrIndex(List.of(getConverter(type).convert(id)));
        final ZoekIndexEntity entity = findZoekIndexEntityByObjectId(id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    public void indexeerDirect(final List<String> objectIds, final ZoekObjectType type) {
        final List<ZoekObject> zoekObjecten = objectIds.stream().map(objectId -> getConverter(type).convert(objectId))
                .collect(Collectors.toList());
        commitToSolrIndex(zoekObjecten);
        objectIds.stream().map(this::findZoekIndexEntityByObjectId).filter(Objects::nonNull)
                .forEach(entity -> entityManager.remove(entity));
    }

    public HerindexerenInfo herindexeren(final ZoekObjectType type) {
        if (herindexerenBezig.contains(type)) {
            LOG.info("Herindexeren is nog bezig ...");
            return new HerindexerenInfo(0, 0, 0);
        }
        herindexerenBezig.add(type);
        try {
            LOG.info("[%s] Starten met herindexeren".formatted(type.toString()));
            deleteAllZoekIndexEntities(type);
            readAllSolrEntitiesAndMarkToRemoveFromSolrIndex(type);
            switch (type) {
                case ZAAK -> markAllZakenForReindexing();
                case TAAK -> markAllTakenForReindexing();
                case DOCUMENT -> markAllInformatieobjectenForReindexing();
            }

            final int removeCount = countZoekIndexEntities(type, REMOVE);
            final int addCount = countZoekIndexEntities(type, ADD);
            final int updateCount = countZoekIndexEntities(type, UPDATE);
            LOG.info("[%s] Herindexeren gereed. Toevoegen: %d, updaten: %d, verwijderen: %d".formatted(
                    type.toString(), addCount, updateCount, removeCount));
            return new HerindexerenInfo(addCount, updateCount, removeCount);
        } finally {
            herindexerenBezig.remove(type);
        }
    }

    public Resultaat indexeer(final int batchGrootte, final ZoekObjectType type) {
        if (herindexerenBezig.contains(type)) {
            LOG.info("[%s] Wachten met indexeren, herindexeren is nog bezig".formatted(type.toString()));
            return new Resultaat();
        }
        final int count = countZoekIndexEntities(type);
        if (count == 0) {
            return new Resultaat();
        }
        final List<ZoekIndexEntity> entities = helper.retrieveEntities(type, batchGrootte);
        LOG.info("[%s] aantal te verwerken: %d (%d)".formatted(type.toString(), entities.size(), count));
        final AbstractZoekObjectConverter<? extends ZoekObject> converter = getConverter(type);
        final List<ZoekObject> addOrUpdateList = entities.stream()
                .filter(zoekIndexEntity -> zoekIndexEntity.getStatus() == ADD || zoekIndexEntity.getStatus() == UPDATE)
                .map(zoekIndexEntity -> convertToZoekObject(zoekIndexEntity, converter))
                .filter(Objects::nonNull)
                .toList();
        final List<String> removeList = entities.stream()
                .filter(zoekIndexEntity -> zoekIndexEntity.getStatus() == REMOVE)
                .map(ZoekIndexEntity::getObjectId)
                .toList();
        commitToSolrIndex(addOrUpdateList);
        removeFromSolrIndex(removeList);
        final var resultaat = new Resultaat(addOrUpdateList.size(), removeList.size(),
                                            entities.size() - addOrUpdateList.size() - removeList.size(),
                                            count - entities.size());
        LOG.info("[%s] geindexeerd: %d, verwijderd: %d, fouten: %d, resterend: %d"
                         .formatted(type.toString(), resultaat.indexed(), resultaat.removed(),
                                    resultaat.errors(), resultaat.remaining()));
        return resultaat;
    }

    private ZoekObject convertToZoekObject(final ZoekIndexEntity zoekIndexEntity,
            final AbstractZoekObjectConverter<? extends ZoekObject> converter) {
        try {
            return converter.convert(zoekIndexEntity.getObjectId());
        } catch (final RuntimeException e) {
            LOG.log(WARNING, "[%s] Exception on object with id '%s'"
                    .formatted(zoekIndexEntity.getType().toString(), zoekIndexEntity.getObjectId()), e);
            return null;
        }
    }

    private void markAllZakenForReindexing() {
        final ZaakListParameters listParameters = new ZaakListParameters();
        listParameters.setOrdering("-identificatie");
        listParameters.setPage(FIRST_PAGE_NUMBER_ZGW_APIS);
        boolean hasNext;
        do {
            final Results<Zaak> results = zrcClientService.listZaken(listParameters);
            results.getResults().forEach(zaak -> markItemForAddOrUpdateInSolrIndex(zaak.getUuid().toString(), ZAAK));
            LOG.info("[%s] Aantal gelezen: %d (%d)".formatted(ZAAK.toString(),
                                                              (listParameters.getPage() - FIRST_PAGE_NUMBER_ZGW_APIS) * NUM_ITEMS_PER_PAGE + results.getResults()
                                                                      .size(), results.getCount()));
            listParameters.setPage(listParameters.getPage() + 1);
            hasNext = results.getNext() != null;
        } while (hasNext);
    }

    private void markAllInformatieobjectenForReindexing() {
        final EnkelvoudigInformatieobjectListParameters listParameters = new EnkelvoudigInformatieobjectListParameters();
        listParameters.setPage(FIRST_PAGE_NUMBER_ZGW_APIS);
        boolean hasNext;
        do {
            final Results<EnkelvoudigInformatieobject> results = drcClientService.listEnkelvoudigInformatieObjecten(
                    listParameters);
            results.getResults().forEach(
                    informatieobject -> markItemForAddOrUpdateInSolrIndex(informatieobject.getUUID().toString(),
                                                                          DOCUMENT));
            LOG.info("[%s] Aantal gelezen: %d (%d)".formatted(DOCUMENT.toString(),
                                                              (listParameters.getPage() - FIRST_PAGE_NUMBER_ZGW_APIS) * NUM_ITEMS_PER_PAGE + results.getResults()
                                                                      .size(), results.getCount()));
            listParameters.setPage(listParameters.getPage() + 1);
            hasNext = results.getNext() != null;
        } while (hasNext);
    }

    private void markAllTakenForReindexing() {
        int page = 0;
        final int maxResults = TAKEN_MAX_RESULTS;
        final long numberOfTasks = takenService.countOpenTasks();
        boolean hasNext;
        do {
            final int firstResult = page * maxResults;
            final List<Task> tasks = takenService.listOpenTasks(TaakSortering.CREATIEDATUM, SorteerRichting.DESCENDING,
                                                                firstResult, maxResults);
            tasks.forEach(task -> markItemForAddOrUpdateInSolrIndex(task.getId(), TAAK));
            if (!tasks.isEmpty()) {
                LOG.info("[%s] Aantal gelezen: %d (%d)".formatted(TAAK.toString(), firstResult + tasks.size(),
                                                                  numberOfTasks));
                hasNext = tasks.size() == TAKEN_MAX_RESULTS;
            } else {
                hasNext = false;
            }
            page++;
        } while (hasNext);
    }

    private void deleteAllZoekIndexEntities(final ZoekObjectType type) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaDelete<ZoekIndexEntity> query = builder.createCriteriaDelete(ZoekIndexEntity.class);
        final Root<ZoekIndexEntity> root = query.from(ZoekIndexEntity.class);
        query.where(builder.equal(root.get("type"), type.toString()));
        entityManager.createQuery(query).executeUpdate();
    }

    private int countZoekIndexEntities(final ZoekObjectType type, final IndexStatus status) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> query = builder.createQuery(Long.class);
        final Root<ZoekIndexEntity> root = query.from(ZoekIndexEntity.class);
        query.select(builder.count(root));
        query.where(builder.and(builder.equal(root.get("type"), type.toString()),
                                builder.equal(root.get("status"), status.toString())));
        final Long result = entityManager.createQuery(query).getSingleResult();
        if (result == null) {
            return 0;
        }
        return result.intValue();
    }

    private int countZoekIndexEntities(final ZoekObjectType type) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> query = builder.createQuery(Long.class);
        final Root<ZoekIndexEntity> root = query.from(ZoekIndexEntity.class);
        query.select(builder.count(root));
        query.where(builder.equal(root.get("type"), type.toString()));
        final Long result = entityManager.createQuery(query).getSingleResult();
        if (result == null) {
            return 0;
        }
        return result.intValue();
    }

    private ZoekIndexEntity findZoekIndexEntityByObjectId(final String objectId) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<ZoekIndexEntity> query = builder.createQuery(ZoekIndexEntity.class);
        final Root<ZoekIndexEntity> root = query.from(ZoekIndexEntity.class);
        query.select(root).where(builder.equal(root.get("objectId"), objectId));
        final List<ZoekIndexEntity> list = entityManager.createQuery(query).getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    private void markItemForAddOrUpdateInSolrIndex(final String objectId, final ZoekObjectType type) {
        final ZoekIndexEntity entity = findZoekIndexEntityByObjectId(objectId);
        if (entity != null) {
            if (entity.getStatus() == ADD) {
                entity.setStatus(UPDATE);
                entityManager.merge(entity);
            }
        } else {
            entityManager.persist(new ZoekIndexEntity(objectId, type, ADD));
        }
    }

    private void markItemForRemovalFromSolrIndex(final String id, final ZoekObjectType type) {
        final ZoekIndexEntity entity = findZoekIndexEntityByObjectId(id);
        if (entity != null) {
            entity.setStatus(REMOVE);
            entityManager.merge(entity);
        } else {
            entityManager.persist(new ZoekIndexEntity(id, type, REMOVE));
        }
    }

    private void readAllSolrEntitiesAndMarkToRemoveFromSolrIndex(final ZoekObjectType type) {
        final SolrQuery query = new SolrQuery("*:*");
        query.setFields("id");
        query.addFilterQuery("type:%s".formatted(type.toString()));
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
            for (final SolrDocument document : response.getResults()) {
                final String objectId = String.valueOf(document.get("id"));
                markItemForRemovalFromSolrIndex(objectId, type);
            }
            final String nextCursorMark = response.getNextCursorMark();
            if (cursorMark.equals(nextCursorMark)) {
                done = true;
            } else {
                cursorMark = nextCursorMark;
            }
        }
    }

    private void commitToSolrIndex(final List<ZoekObject> zoekObjecten) {
        final List<ZoekObject> zoekObjectenWithoutNulls = zoekObjecten.stream().filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(zoekObjectenWithoutNulls)) {
            try {
                solrClient.addBeans(zoekObjectenWithoutNulls);
                solrClient.commit();
            } catch (final IOException | SolrServerException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void removeFromSolrIndex(final List<String> ids) {
        if (CollectionUtils.isNotEmpty(ids)) {
            try {
                solrClient.deleteById(ids);
                solrClient.commit();
            } catch (final IOException | SolrServerException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private AbstractZoekObjectConverter<? extends ZoekObject> getConverter(ZoekObjectType type) {
        for (AbstractZoekObjectConverter<? extends ZoekObject> converter : converterInstances) {
            if (converter.supports(type)) {
                return converter;
            }
        }
        throw new RuntimeException("No converter found for '%s'".formatted(type));
    }

    public void addZaak(final UUID zaakUUID, boolean inclTaken, boolean inclInformatieobjecten) {
        markItemForAddOrUpdateInSolrIndex(zaakUUID.toString(), ZAAK);
        if (inclTaken) {
            takenService.listTasksForZaak(zaakUUID).stream()
                    .map(TaskInfo::getId)
                    .forEach(this::addTaak);
        }
        if (inclInformatieobjecten) {
            zrcClientService.listZaakinformatieobjecten(zrcClientService.readZaak(zaakUUID)).stream()
                    .map(ZaakInformatieobject::getInformatieobject)
                    .map(UriUtil::uuidFromURI)
                    .forEach(this::addInformatieobject);
        }
    }

    public void addZakenByZaaktype(final UUID zaaktypeUUID, boolean inclTaken, boolean inclInformatieobjecten) {
        final ZaakListParameters parameters = new ZaakListParameters();
        parameters.setZaaktype(ztcClientService.readZaaktype(zaaktypeUUID).getUrl());
        zrcClientService.listZaken(parameters).getResults().stream()
                .map(Zaak::getUuid)
                .forEach(uuid -> addZaak(uuid, true, true));
    }

    public void removeZaak(final UUID zaakUUID) {
        markItemForRemovalFromSolrIndex(zaakUUID.toString(), ZAAK);
    }

    public void addInformatieobject(final UUID informatieobjectUUID) {
        markItemForAddOrUpdateInSolrIndex(informatieobjectUUID.toString(), DOCUMENT);
    }

    public void addInformatieobjectByZaakinformatieobject(final UUID zaakinformatieobjectUUID) {
        final ZaakInformatieobject zaakInformatieobject = zrcClientService.readZaakinformatieobject(
                zaakinformatieobjectUUID);
        markItemForAddOrUpdateInSolrIndex(
                UriUtil.uuidFromURI(zaakInformatieobject.getInformatieobject()).toString(), DOCUMENT);
    }


    public void removeInformatieobject(final UUID informatieobjectUUID) {
        markItemForRemovalFromSolrIndex(informatieobjectUUID.toString(), DOCUMENT);
    }

    public void addTaak(final String taskID) {
        markItemForAddOrUpdateInSolrIndex(taskID, TAAK);
    }

    public void removeTaak(final String taskID) {
        markItemForRemovalFromSolrIndex(taskID, TAAK);
    }

}
