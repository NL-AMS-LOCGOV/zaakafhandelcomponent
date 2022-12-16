/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import net.atos.zac.zoeken.model.index.ZoekIndexEntity;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

public class IndexeerServiceHelper {

    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<ZoekIndexEntity> retrieveEntities(final ZoekObjectType type, final int rows) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<ZoekIndexEntity> query = builder.createQuery(ZoekIndexEntity.class);
        final Root<ZoekIndexEntity> root = query.from(ZoekIndexEntity.class);
        query.select(root).where(builder.equal(root.get("type"), type.toString()));
        query.orderBy(builder.asc(root.get("id")));
        final TypedQuery<ZoekIndexEntity> emQuery = entityManager.createQuery(query);
        emQuery.setMaxResults(rows);
        final List<ZoekIndexEntity> entities = emQuery.getResultList();
        entities.forEach(entity -> entityManager.remove(entity));
        return entities;
    }
}
