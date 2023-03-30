/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken;

import static net.atos.zac.zoeken.model.index.IndexStatus.ADD;
import static net.atos.zac.zoeken.model.index.IndexStatus.REMOVE;
import static net.atos.zac.zoeken.model.index.IndexStatus.UPDATE;

import java.util.List;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import net.atos.zac.zoeken.model.index.IndexStatus;
import net.atos.zac.zoeken.model.index.ZoekIndexEntity;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

public class IndexeerServiceHelper {

    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;

    private void markObjectForIndexing(final String objectId, final ZoekObjectType objectType, final boolean reindex) {
        final ZoekIndexEntity entity = findMarkedObject(objectId);
        if (entity != null) {
            if (entity.getStatus() == (reindex ? REMOVE : ADD)) {
                entity.setStatus(UPDATE);
                entityManager.merge(entity);
            }
        } else {
            entityManager.persist(new ZoekIndexEntity(objectId, objectType, ADD));
        }
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void markObjectForIndexing(final String objectId, final ZoekObjectType objectType) {
        markObjectForIndexing(objectId, objectType, false);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void markObjectsForReindexing(final Stream<String> objectIds, final ZoekObjectType objectType) {
        objectIds.forEach(id -> markObjectForIndexing(id, objectType, true));
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void markObjectForRemoval(final String objectId, final ZoekObjectType objectType) {
        final ZoekIndexEntity entity = findMarkedObject(objectId);
        if (entity != null) {
            entity.setStatus(REMOVE);
            entityManager.merge(entity);
        } else {
            entityManager.persist(new ZoekIndexEntity(objectId, objectType, REMOVE));
        }
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void markObjectsForRemoval(final Stream<String> objectIds, final ZoekObjectType objectType) {
        objectIds.forEach(id -> markObjectForRemoval(id, objectType));
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void removeMark(final String objectId) {
        final ZoekIndexEntity entity = findMarkedObject(objectId);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void removeMarks(final Stream<String> objectIds) {
        objectIds.forEach(this::removeMark);
    }

    private ZoekIndexEntity findMarkedObject(final String objectId) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<ZoekIndexEntity> query = builder.createQuery(ZoekIndexEntity.class);
        final Root<ZoekIndexEntity> root = query.from(ZoekIndexEntity.class);
        query.where(builder.equal(root.get(ZoekIndexEntity.OBJECT_ID), objectId));
        final List<ZoekIndexEntity> list = entityManager.createQuery(query).getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    public List<ZoekIndexEntity> retrieveMarkedObjects(final ZoekObjectType objectType, final int rows) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<ZoekIndexEntity> query = builder.createQuery(ZoekIndexEntity.class);
        final Root<ZoekIndexEntity> root = query.from(ZoekIndexEntity.class);
        query.where(builder.equal(root.get(ZoekIndexEntity.TYPE), objectType.toString()));
        final TypedQuery<ZoekIndexEntity> emQuery = entityManager.createQuery(query);
        emQuery.setMaxResults(rows);
        return emQuery.getResultList();
    }

    public long countMarkedObjects(final ZoekObjectType objectType, final IndexStatus status) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> query = builder.createQuery(Long.class);
        final Root<ZoekIndexEntity> root = query.from(ZoekIndexEntity.class);
        final Predicate predicate = builder.equal(root.get(ZoekIndexEntity.TYPE), objectType.toString());
        query.select(builder.count(root));
        query.where(status != null
                            ? builder.and(predicate, builder.equal(root.get(ZoekIndexEntity.STATUS), status.toString()))
                            : predicate);
        return entityManager.createQuery(query).getSingleResult();
    }

    public long countMarkedObjects(final ZoekObjectType type) {
        return countMarkedObjects(type, null);
    }
}
