/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.gebruikersvoorkeuren;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;

import net.atos.zac.gebruikersvoorkeuren.model.Zoekopdracht;
import net.atos.zac.gebruikersvoorkeuren.model.ZoekopdrachtListParameters;

@ApplicationScoped
@Transactional
public class GebruikersvoorkeurenService {

    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;


    public Zoekopdracht createZoekopdracht(final Zoekopdracht zoekOpdracht) {
        entityManager.persist(zoekOpdracht);
        return zoekOpdracht;
    }

    public Zoekopdracht findZoekopdracht(final long id) {
        return entityManager.find(Zoekopdracht.class, id);
    }

    public List<Zoekopdracht> listZoekopdrachten(final ZoekopdrachtListParameters listParameters) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Zoekopdracht> query = builder.createQuery(Zoekopdracht.class);
        final Root<Zoekopdracht> root = query.from(Zoekopdracht.class);
        final List<Predicate> predicates = new ArrayList<>();
        if (StringUtils.isNotBlank(listParameters.getLijstID())) {
            predicates.add(builder.equal(root.get(Zoekopdracht.LIJST_ID), listParameters.getLijstID()));
        }
        if (StringUtils.isNotBlank(listParameters.getMedewerkerID())) {
            predicates.add(builder.equal(root.get(Zoekopdracht.MEDEWERKER_ID), listParameters.getMedewerkerID()));
        }
        query.where(builder.and(predicates.toArray(new Predicate[0])));
        final TypedQuery<Zoekopdracht> emQuery = entityManager.createQuery(query);
        return emQuery.getResultList();
    }

    public void deleteZoekopdracht(final Long id) {
        final Zoekopdracht zoekOpdracht = findZoekopdracht(id);
        if (zoekOpdracht != null) {
            entityManager.remove(zoekOpdracht);
        }
    }

}
