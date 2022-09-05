/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken;

import static net.atos.client.zgw.shared.ZGWApiService.FIRST_PAGE_NUMBER_ZGW_APIS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.CursorMarkParams;
import org.eclipse.microprofile.config.ConfigProvider;
import org.flowable.task.api.Task;

import net.atos.client.zgw.shared.model.Results;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.ZaakListParameters;
import net.atos.zac.app.taken.model.TaakSortering;
import net.atos.zac.flowable.TaskService;
import net.atos.zac.shared.model.SorteerRichting;
import net.atos.zac.zoeken.converter.AbstractZoekObjectConverter;
import net.atos.zac.zoeken.model.ZoekObject;
import net.atos.zac.zoeken.model.index.HerindexeerInfo;
import net.atos.zac.zoeken.model.index.IndexStatus;
import net.atos.zac.zoeken.model.index.ZoekIndexEntity;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

@ApplicationScoped
@Transactional
public class IndexeerService {

    private static final String SOLR_CORE = "zac";

    private static final Logger LOG = Logger.getLogger(IndexeerService.class.getName());

    @Inject
    @Any
    private Instance<AbstractZoekObjectConverter<? extends ZoekObject>> converterInstances;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private TaskService taskService;

    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;

    private SolrClient solrClient;

    public IndexeerService() {
        final String solrUrl = ConfigProvider.getConfig().getValue("solr.url", String.class);
        solrClient = new HttpSolrClient.Builder(String.format("%s/solr/%s", solrUrl, SOLR_CORE)).build();
    }

    public void indexeerDirect(final String id, final ZoekObjectType type) {
        addToSolr(List.of(getConverter(type).convert(id)));
        final ZoekIndexEntity entity = findEntityByObjectId(id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    public void indexeerDirect(final List<String> objectIds, final ZoekObjectType type) {
        final List<ZoekObject> zoekObjecten = objectIds.stream().map(objectId -> getConverter(type).convert(objectId)).collect(Collectors.toList());
        addToSolr(zoekObjecten);
        objectIds.stream().map(this::findEntityByObjectId).filter(Objects::nonNull).forEach(entity -> entityManager.remove(entity));
    }

    public HerindexeerInfo herindexeren(final ZoekObjectType type) {
        deleteEntities(type);
        processSolrIndex(type);
        switch (type) {
            case ZAAK -> processZaken();
            case TAAK -> processTaken();
        }

        // In de Solr-index, maar niet (meer) in Open-Zaak
        final int delete = deleteEntities(type, IndexStatus.INDEXED);
        final int add = countEntities(type, IndexStatus.ADD);
        final int update = countEntities(type, IndexStatus.UPDATE);
        return new HerindexeerInfo(add, update, delete);
    }

    public int indexeer(int batchGrootte, ZoekObjectType type) {
        if (isHerindexeren(type)) {
            LOG.info("[%s] Wachten met indexeren, herindexeren is nog bezig".formatted(type.toString()));
            return batchGrootte;
        }
        LOG.info("[%s] aantal te indexeren: %d".formatted(type.toString(), countEntities(type)));
        List<ZoekIndexEntity> entities = listEntities(type, batchGrootte);
        final List<ZoekObject> addList = new ArrayList<>();
        final List<String> deleteList = new ArrayList<>();
        final AbstractZoekObjectConverter<? extends ZoekObject> converter = getConverter(type);
        entities.forEach(zoekIndexEntity -> {
            try {
                switch (IndexStatus.valueOf(zoekIndexEntity.getStatus())) {
                    case ADD, UPDATE -> addList.add(converter.convert(zoekIndexEntity.getObjectId()));
                    case REMOVE -> deleteList.add(zoekIndexEntity.getObjectId());
                    case INDEXED -> {
                        // skip..
                    }
                }
            } catch (RuntimeException e) {
                LOG.warning("[%s] Skipped %s: %s".formatted(type.toString(), zoekIndexEntity.getObjectId(), e.getMessage()));
                entityManager.remove(zoekIndexEntity);
            }
        });
        addToSolr(addList);
        deleteFromSolr(deleteList);
        entities.forEach(entity -> entityManager.remove(entity));
        return countEntities(type);
    }

    private void processZaken() {
        final ZaakListParameters listParameters = new ZaakListParameters();
        listParameters.setOrdering("-identificatie");
        listParameters.setPage(FIRST_PAGE_NUMBER_ZGW_APIS);
        boolean hasNext = true;
        while (hasNext) {
            final Results<Zaak> results = zrcClientService.listZaken(listParameters);
            results.getResults().forEach(zaak -> createEntity(zaak.getUuid().toString(), ZoekObjectType.ZAAK));
            hasNext = results.getNext() != null;
            listParameters.setPage(listParameters.getPage() + 1);
        }
    }

    private void processTaken() {
        int page = 0;
        final int maxResults = 50;
        boolean hasNext = true;
        while (hasNext) {
            final int firstResult = page * maxResults;
            final List<Task> tasks = taskService.listOpenTasks(TaakSortering.CREATIEDATUM, SorteerRichting.DESCENDING, firstResult, maxResults);
            tasks.forEach(taak -> createEntity(taak.getId(), ZoekObjectType.TAAK));
            page++;
            hasNext = CollectionUtils.isNotEmpty(tasks);
        }
    }

    private List<ZoekIndexEntity> listEntities(final ZoekObjectType type, final int rows) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<ZoekIndexEntity> query = builder.createQuery(ZoekIndexEntity.class);
        final Root<ZoekIndexEntity> root = query.from(ZoekIndexEntity.class);
        query.select(root).where(builder.equal(root.get("type"), type.toString()));
        query.orderBy(builder.asc(root.get("id")));
        final TypedQuery<ZoekIndexEntity> emQuery = entityManager.createQuery(query);
        emQuery.setMaxResults(rows);
        return emQuery.getResultList();
    }

    private void deleteEntities(final ZoekObjectType type) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaDelete<ZoekIndexEntity> query = builder.createCriteriaDelete(ZoekIndexEntity.class);
        final Root<ZoekIndexEntity> root = query.from(ZoekIndexEntity.class);
        query.where(builder.equal(root.get("type"), type.toString()));
        entityManager.createQuery(query).executeUpdate();
    }

    private int deleteEntities(final ZoekObjectType type, final IndexStatus status) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaDelete<ZoekIndexEntity> query = builder.createCriteriaDelete(ZoekIndexEntity.class);
        final Root<ZoekIndexEntity> root = query.from(ZoekIndexEntity.class);
        query.where(builder.and(
                builder.equal(root.get("type"), type.toString()),
                builder.equal(root.get("status"), status.toString())
        ));
        return entityManager.createQuery(query).executeUpdate();
    }

    public int countEntities(final ZoekObjectType type, final IndexStatus status) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> query = builder.createQuery(Long.class);
        final Root<ZoekIndexEntity> root = query.from(ZoekIndexEntity.class);
        query.select(builder.count(root));
        query.where(builder.and(
                builder.equal(root.get("type"), type.toString()),
                builder.equal(root.get("status"), status.toString())
        ));
        final Long result = entityManager.createQuery(query).getSingleResult();
        if (result == null) {
            return 0;
        }
        return result.intValue();
    }

    public int countEntities(final ZoekObjectType type) {
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

    private ZoekIndexEntity findEntityByObjectId(final String objectId) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<ZoekIndexEntity> query = builder.createQuery(ZoekIndexEntity.class);
        final Root<ZoekIndexEntity> root = query.from(ZoekIndexEntity.class);
        query.select(root).where(builder.equal(root.get("objectId"), objectId));
        final List<ZoekIndexEntity> list = entityManager.createQuery(query).getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    private void createEntity(final String objectId, final ZoekObjectType type) {
        final ZoekIndexEntity entity = findEntityByObjectId(objectId);
        if (entity != null) {
            entity.setStatus(IndexStatus.UPDATE);
            entityManager.merge(entity);
        } else {
            entityManager.persist(new ZoekIndexEntity(objectId, type, IndexStatus.ADD));
        }
    }

    private void createEntity(final String id, final ZoekObjectType type, final IndexStatus status) {
        final ZoekIndexEntity entity = findEntityByObjectId(id);
        if (entity != null) {
            entity.setStatus(status);
            entityManager.merge(entity);
        } else {
            entityManager.persist(new ZoekIndexEntity(id, type, status));
        }
    }

    private boolean isHerindexeren(final ZoekObjectType type) {
        return countEntities(type, IndexStatus.INDEXED) > 0;
    }

    private void processSolrIndex(final ZoekObjectType type) {
        final SolrQuery query = new SolrQuery("*:*");
        query.setFields("id");
        query.addFilterQuery("type:%s".formatted(type.toString()));
        query.addSort("id", SolrQuery.ORDER.asc);
        query.setRows(100);
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
            String nextCursorMark = response.getNextCursorMark();
            for (SolrDocument document : response.getResults()) {
                final String objectId = String.valueOf(document.get("id"));
                createEntity(objectId, type, IndexStatus.INDEXED);
            }
            if (cursorMark.equals(nextCursorMark)) {
                done = true;
            }
            cursorMark = nextCursorMark;
        }
    }

    private void addToSolr(final List<ZoekObject> zoekObjecten) {
        if (CollectionUtils.isNotEmpty(zoekObjecten)) {
            try {
                solrClient.addBeans(zoekObjecten);
                solrClient.commit();
            } catch (final IOException | SolrServerException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void deleteFromSolr(final List<String> ids) {
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

    public void addZaak(final UUID zaakUUID, boolean inclusieTaken) {
        createEntity(zaakUUID.toString(), ZoekObjectType.ZAAK);
        if (inclusieTaken) {
            taskService.listTasksForCase(zaakUUID).forEach(taskInfo -> {
                addTaak(taskInfo.getId());
            });
        }
    }

    public void removeZaak(final UUID zaakUUID) {
        createEntity(zaakUUID.toString(), ZoekObjectType.ZAAK, IndexStatus.REMOVE);
    }

    public void addTaak(final String taskID) {
        createEntity(taskID, ZoekObjectType.TAAK);
    }

    public void removeTaak(final String taskID) {
        createEntity(taskID, ZoekObjectType.TAAK, IndexStatus.REMOVE);
    }

}
