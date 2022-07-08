/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.gebruikersvoorkeuren;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import net.atos.zac.gebruikersvoorkeuren.model.Zoekopdracht;
import net.atos.zac.gebruikersvoorkeuren.model.ZoekopdrachtListParameters;

@ApplicationScoped
@Transactional
public class GebruikersvoorkeurenService {

    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;

    public Zoekopdracht createZoekopdracht(final Zoekopdracht zoekopdracht) {
        if (zoekopdracht.getId() != null) {
            return entityManager.merge(zoekopdracht);
        } else {
            if (existsZoekopdracht(zoekopdracht)) {
                throw new RuntimeException("Er bestaat al een zoekopdracht met naam '%s' en lijst '%s' en medewerker: '%s'"
                                                   .formatted(zoekopdracht.getNaam(), zoekopdracht.getLijstID(), zoekopdracht.getMedewerkerID()));
            }
            zoekopdracht.setActief(true);
            entityManager.persist(zoekopdracht);
            setActief(zoekopdracht);
        }
        return zoekopdracht;
    }

    public Zoekopdracht findZoekopdracht(final long id) {
        return entityManager.find(Zoekopdracht.class, id);
    }

    public List<Zoekopdracht> listZoekopdrachten(final ZoekopdrachtListParameters listParameters) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Zoekopdracht> query = builder.createQuery(Zoekopdracht.class);
        final Root<Zoekopdracht> root = query.from(Zoekopdracht.class);
        final List<Predicate> predicates = new ArrayList<>();
        if (listParameters.getLijstID() != null) {
            predicates.add(builder.equal(root.get(Zoekopdracht.LIJST_ID), listParameters.getLijstID()));
        }
        if (StringUtils.isNotBlank(listParameters.getMedewerkerID())) {
            predicates.add(builder.equal(root.get(Zoekopdracht.MEDEWERKER_ID), listParameters.getMedewerkerID()));
        }
        query.where(builder.and(predicates.toArray(new Predicate[0])));
        final TypedQuery<Zoekopdracht> emQuery = entityManager.createQuery(query);
        return emQuery.getResultList();
    }

    private boolean existsZoekopdracht(final Zoekopdracht zoekopdracht) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Zoekopdracht> query = builder.createQuery(Zoekopdracht.class);
        final Root<Zoekopdracht> root = query.from(Zoekopdracht.class);
        final List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get(Zoekopdracht.LIJST_ID), zoekopdracht.getLijstID()));
        predicates.add(builder.equal(root.get(Zoekopdracht.MEDEWERKER_ID), zoekopdracht.getMedewerkerID()));
        predicates.add(builder.equal(builder.lower(root.get(Zoekopdracht.NAAM)), zoekopdracht.getNaam().toLowerCase(Locale.ROOT)));
        query.where(builder.and(predicates.toArray(new Predicate[0])));
        final TypedQuery<Zoekopdracht> emQuery = entityManager.createQuery(query);
        return CollectionUtils.isNotEmpty(emQuery.getResultList());
    }

    public void deleteZoekopdracht(final Long id) {
        final Zoekopdracht zoekOpdracht = findZoekopdracht(id);
        if (zoekOpdracht != null) {
            entityManager.remove(zoekOpdracht);
        }
    }

    public void setActief(final Zoekopdracht zoekopdracht) {
        final List<Zoekopdracht> zoekopdrachten = listZoekopdrachten(new ZoekopdrachtListParameters(zoekopdracht.getLijstID(), zoekopdracht.getMedewerkerID()));
        zoekopdrachten.forEach(z -> {
            final boolean huidigeWaarde = z.isActief();
            z.setActief(z.getId().equals(zoekopdracht.getId()));
            if (huidigeWaarde != z.isActief()) {
                entityManager.merge(z);
            }
        });
    }

    public void removeActief(final ZoekopdrachtListParameters zoekopdrachtListParameters) {
        listZoekopdrachten(zoekopdrachtListParameters).stream().filter(Zoekopdracht::isActief).forEach(zoekopdracht -> {
            zoekopdracht.setActief(false);
            entityManager.merge(zoekopdracht);
        });
    }
}
