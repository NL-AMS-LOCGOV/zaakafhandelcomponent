/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.notities;

import static net.atos.zac.notities.model.Notitie.ZAAK_UUID;
import static net.atos.zac.util.ValidationUtil.valideerObject;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import net.atos.zac.notities.model.Notitie;

@ApplicationScoped
@Transactional
public class NotitieService {

    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;

    public Notitie createNotitie(final Notitie notitie) {
        valideerObject(notitie);
        entityManager.persist(notitie);
        return notitie;
    }

    public List<Notitie> listNotitiesForZaak(final UUID zaakUUID) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Notitie> query = builder.createQuery(Notitie.class);
        final Root<Notitie> root = query.from(Notitie.class);
        query.select(root).where(builder.equal(root.get(ZAAK_UUID), zaakUUID));
        return entityManager.createQuery(query).getResultList();
    }

    public Notitie updateNotitie(final Notitie notitie) {
        valideerObject(notitie);
        return entityManager.merge(notitie);
    }

    public void deleteNotitie(final Long notitieId) {
        final Notitie notitie = entityManager.find(Notitie.class, notitieId);
        entityManager.remove(notitie);
    }
}
