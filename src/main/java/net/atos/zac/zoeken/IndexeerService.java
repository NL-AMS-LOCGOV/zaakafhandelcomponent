/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken;

import static net.atos.client.zgw.shared.ZGWApiService.FIRST_PAGE_NUMBER_ZGW_APIS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
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
import org.apache.commons.lang3.NotImplementedException;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.CursorMarkParams;
import org.eclipse.microprofile.config.ConfigProvider;

import net.atos.client.zgw.shared.model.Results;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.ZaakListParameters;
import net.atos.zac.zoeken.converter.ZaakZoekObjectConverter;
import net.atos.zac.zoeken.model.ZaakZoekObject;
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
    private ZaakZoekObjectConverter zaakZoekObjectConverter;

    @Inject
    private ZRCClientService zrcClientService;

    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;

    private SolrClient solrClient;

    public IndexeerService() {
        final String solrUrl = ConfigProvider.getConfig().getValue("solr.url", String.class);
        solrClient = new HttpSolrClient.Builder(String.format("%s/solr/%s", solrUrl, SOLR_CORE)).build();
    }

    public void indexeerDirect(final UUID uuid, final ZoekObjectType type) {
        if (type == ZoekObjectType.ZAAK) {
            addToSolr(List.of(zaakZoekObjectConverter.convert(uuid)));
            final ZoekIndexEntity entity = findEntity(uuid);
            if (entity != null) {
                entityManager.remove(entity);
            }
        }
    }

    public void indexeerDirect(final List<UUID> uuids, final ZoekObjectType type) {
        if (type == ZoekObjectType.ZAAK) {
            List<ZaakZoekObject> zaken = new ArrayList<>();
            uuids.forEach(uuid -> zaken.add(zaakZoekObjectConverter.convert(uuid)));
            addToSolr(zaken);
            uuids.forEach(uuid -> {
                final ZoekIndexEntity entity = findEntity(uuid);
                if (entity != null) {
                    entityManager.remove(entity);
                }
            });
        }
    }

    public HerindexeerInfo herindexeren(final ZoekObjectType type) {
        final HerindexeerInfo info = new HerindexeerInfo();
        deleteEntities(type);
        processSolrIndex(type);
        switch (type) {
            case ZAAK -> processZaken();
            case INFORMATIE_OBJECT -> throw new NotImplementedException();
        }

        // In de Solr-index, maar niet (meer) in Open-Zaak
        info.setVerwijderen(deleteEntities(type, IndexStatus.INDEXED));

        info.setToevoegen(countEntities(type, IndexStatus.ADD));
        info.setHerindexeren(countEntities(type, IndexStatus.UPDATE));

        return info;
    }

    public int indexeerZaken(int batchGrootte) {
        if (isHerindexeren(ZoekObjectType.ZAAK)) {
            LOG.info("Wachten met indexeren, herindexeren is nog bezig");
            return batchGrootte;
        }
        LOG.info("aantal te indexeren: " + countEntities(ZoekObjectType.ZAAK));
        List<ZoekIndexEntity> entities = listEntities(ZoekObjectType.ZAAK, batchGrootte);
        final List<ZaakZoekObject> addList = new ArrayList<>();
        final List<String> deleteList = new ArrayList<>();
        entities.forEach(zoekIndexEntity -> {
            try {
                switch (IndexStatus.valueOf(zoekIndexEntity.getStatus())) {
                    case ADD, UPDATE -> addList.add(zaakZoekObjectConverter.convert(zoekIndexEntity.getUuid()));
                    case REMOVE -> deleteList.add(zoekIndexEntity.getUuid().toString());
                    case INDEXED -> {
                        // skip..
                    }
                }
            } catch (RuntimeException e) {
                LOG.warning("Skipped %s: %s".formatted(zoekIndexEntity.getUuid(), e.getMessage()));
                entityManager.remove(zoekIndexEntity);
            }
        });
        addToSolr(addList);
        deleteFromSolr(deleteList);
        entities.forEach(entity -> entityManager.remove(entity));
        return countEntities(ZoekObjectType.ZAAK);
    }

    private void processZaken() {
        ZaakListParameters listParameters = new ZaakListParameters();
        listParameters.setOrdering("-identificatie");
        listParameters.setPage(FIRST_PAGE_NUMBER_ZGW_APIS);
        boolean hasNext = true;
        while (hasNext) {
            final Results<Zaak> results = zrcClientService.listZaken(listParameters);
            results.getResults().forEach(zaak -> createEntity(zaak.getUuid(), ZoekObjectType.ZAAK));
            hasNext = results.getNext() != null;
            listParameters.setPage(listParameters.getPage() + 1);
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

    private ZoekIndexEntity findEntity(final UUID uuid) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<ZoekIndexEntity> query = builder.createQuery(ZoekIndexEntity.class);
        final Root<ZoekIndexEntity> root = query.from(ZoekIndexEntity.class);
        query.select(root).where(builder.equal(root.get("uuid"), uuid));
        final List<ZoekIndexEntity> list = entityManager.createQuery(query).getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    private void createEntity(final UUID uuid, final ZoekObjectType type) {
        final ZoekIndexEntity entity = findEntity(uuid);
        if (entity != null) {
            entity.setStatus(IndexStatus.UPDATE);
            entityManager.merge(entity);
        } else {
            entityManager.persist(new ZoekIndexEntity(uuid, type, IndexStatus.ADD));
        }
    }

    private void createEntity(final UUID uuid, final ZoekObjectType type, final IndexStatus status) {
        final ZoekIndexEntity entity = findEntity(uuid);
        if (entity != null) {
            entity.setStatus(status);
            entityManager.merge(entity);
        } else {
            entityManager.persist(new ZoekIndexEntity(uuid, type, status));
        }
    }

    private boolean isHerindexeren(final ZoekObjectType type) {
        return countEntities(type, IndexStatus.INDEXED) > 0;
    }

    private void processSolrIndex(final ZoekObjectType type) {
        final SolrQuery query = new SolrQuery("*:*");
        query.setFields("uuid");
        query.addFilterQuery("type:%s".formatted(type.toString()));
        query.addSort("uuid", SolrQuery.ORDER.asc);
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
                final UUID uuid = UUID.fromString(String.valueOf(document.get("uuid")));
                createEntity(uuid, type, IndexStatus.INDEXED);
            }
            if (cursorMark.equals(nextCursorMark)) {
                done = true;
            }
            cursorMark = nextCursorMark;
        }
    }

    private void addToSolr(final List<ZaakZoekObject> zaken) {
        if (CollectionUtils.isNotEmpty(zaken)) {
            try {
                solrClient.addBeans(zaken);
                solrClient.commit();
            } catch (final IOException | SolrServerException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void deleteFromSolr(final List<String> uuids) {
        if (CollectionUtils.isNotEmpty(uuids)) {
            try {
                solrClient.deleteById(uuids);
                solrClient.commit();
            } catch (final IOException | SolrServerException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void addZaak(final UUID zaakUUID) {
        createEntity(zaakUUID, ZoekObjectType.ZAAK);
    }

    public void removeZaak(final UUID zaakUUID) {
        createEntity(zaakUUID, ZoekObjectType.ZAAK, IndexStatus.REMOVE);
    }
}
