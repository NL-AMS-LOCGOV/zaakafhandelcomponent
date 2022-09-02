/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zaaksturing;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import net.atos.zac.zaaksturing.model.ReferentieTabel;

@ApplicationScoped
@Transactional
public class ReferentieTabelService {

    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;

    public List<ReferentieTabel> listReferentieTabellen() {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<ReferentieTabel> query = builder.createQuery(ReferentieTabel.class);
        final Root<ReferentieTabel> root = query.from(ReferentieTabel.class);
        query.orderBy(builder.asc(root.get("naam")));
        query.select(root);
        return entityManager.createQuery(query).getResultList();
    }

    public ReferentieTabel readReferentieTabel(final long id) {
        final ReferentieTabel referentieTabel = entityManager.find(ReferentieTabel.class, id);
        if (referentieTabel != null) {
            return referentieTabel;
        } else {
            throw new RuntimeException(String.format("%s with id=%d not found", ReferentieTabel.class.getSimpleName(), id));
        }
    }

    public ReferentieTabel readReferentieTabel(final ReferentieTabel.Systeem known) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<ReferentieTabel> query = builder.createQuery(ReferentieTabel.class);
        final Root<ReferentieTabel> root = query.from(ReferentieTabel.class);
        query.select(root).where(builder.equal(root.get("code"), known.name()));
        final List<ReferentieTabel> resultList = entityManager.createQuery(query).getResultList();
        if (!resultList.isEmpty()) {
            return resultList.get(0);
        } else {
            throw new RuntimeException(String.format("%s %s not found", ReferentieTabel.class.getSimpleName(), known.name()));
        }
    }
}
