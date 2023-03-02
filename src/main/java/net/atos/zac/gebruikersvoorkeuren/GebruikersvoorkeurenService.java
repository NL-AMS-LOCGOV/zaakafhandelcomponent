/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.gebruikersvoorkeuren;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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

import net.atos.zac.gebruikersvoorkeuren.model.DashboardCardInstelling;
import net.atos.zac.gebruikersvoorkeuren.model.TabelInstellingen;
import net.atos.zac.gebruikersvoorkeuren.model.Werklijst;
import net.atos.zac.gebruikersvoorkeuren.model.Zoekopdracht;
import net.atos.zac.gebruikersvoorkeuren.model.ZoekopdrachtListParameters;
import net.atos.zac.signalering.SignaleringenService;
import net.atos.zac.signalering.model.SignaleringInstellingen;

@ApplicationScoped
@Transactional
public class GebruikersvoorkeurenService {

    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;

    @Inject
    private SignaleringenService signaleringenService;

    public Zoekopdracht createZoekopdracht(final Zoekopdracht zoekopdracht) {
        if (zoekopdracht.getId() != null) {
            return entityManager.merge(zoekopdracht);
        } else {
            if (existsZoekopdracht(zoekopdracht)) {
                throw new RuntimeException(
                        "Er bestaat al een zoekopdracht met naam '%s' en lijst '%s' en medewerker: '%s'"
                                .formatted(zoekopdracht.getNaam(), zoekopdracht.getLijstID(),
                                           zoekopdracht.getMedewerkerID()));
            }
            zoekopdracht.setActief(true);
            entityManager.persist(zoekopdracht);
            setActief(zoekopdracht);
        }
        return zoekopdracht;
    }

    public Optional<Zoekopdracht> findZoekopdracht(final long id) {
        final var zoekopdracht = entityManager.find(Zoekopdracht.class, id);
        return zoekopdracht != null ? Optional.of(zoekopdracht) : Optional.empty();
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

    public TabelInstellingen readTabelInstellingen(final Werklijst lijstID, final String medewerkerID) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<TabelInstellingen> query = builder.createQuery(TabelInstellingen.class);
        final Root<TabelInstellingen> root = query.from(TabelInstellingen.class);
        final List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get(TabelInstellingen.LIJST_ID), lijstID));
        predicates.add(builder.equal(root.get(TabelInstellingen.MEDEWERKER_ID), medewerkerID));
        query.where(builder.and(predicates.toArray(new Predicate[0])));
        final List<TabelInstellingen> resultList = entityManager.createQuery(query).getResultList();
        if (!resultList.isEmpty()) {
            return resultList.get(0);
        } else {
            final TabelInstellingen tabelInstellingen = new TabelInstellingen();
            tabelInstellingen.setLijstID(lijstID);
            tabelInstellingen.setMedewerkerID(medewerkerID);
            tabelInstellingen.setAantalPerPagina(TabelInstellingen.AANTAL_PER_PAGINA_DEFAULT);
            return tabelInstellingen;
        }
    }

    public void updateTabelInstellingen(final TabelInstellingen tabelInstellingen) {
        final TabelInstellingen bestaandeTabelInstellingen = readTabelInstellingen(tabelInstellingen.getLijstID(), tabelInstellingen.getMedewerkerID());
        bestaandeTabelInstellingen.setAantalPerPagina(tabelInstellingen.getAantalPerPagina());
        entityManager.merge(bestaandeTabelInstellingen);
    }

    private boolean existsZoekopdracht(final Zoekopdracht zoekopdracht) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Zoekopdracht> query = builder.createQuery(Zoekopdracht.class);
        final Root<Zoekopdracht> root = query.from(Zoekopdracht.class);
        final List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get(Zoekopdracht.LIJST_ID), zoekopdracht.getLijstID()));
        predicates.add(builder.equal(root.get(Zoekopdracht.MEDEWERKER_ID), zoekopdracht.getMedewerkerID()));
        predicates.add(builder.equal(builder.lower(root.get(Zoekopdracht.NAAM)),
                                     zoekopdracht.getNaam().toLowerCase(Locale.ROOT)));
        query.where(builder.and(predicates.toArray(new Predicate[0])));
        final TypedQuery<Zoekopdracht> emQuery = entityManager.createQuery(query);
        return CollectionUtils.isNotEmpty(emQuery.getResultList());
    }

    public void deleteZoekopdracht(final Long id) {
        findZoekopdracht(id).ifPresent(entityManager::remove);
    }

    public void setActief(final Zoekopdracht zoekopdracht) {
        final List<Zoekopdracht> zoekopdrachten = listZoekopdrachten(
                new ZoekopdrachtListParameters(zoekopdracht.getLijstID(), zoekopdracht.getMedewerkerID()));
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

    public List<DashboardCardInstelling> listDashboardCards(final String medewerkerId) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<DashboardCardInstelling> query = builder.createQuery(DashboardCardInstelling.class);
        final Root<DashboardCardInstelling> root = query.from(DashboardCardInstelling.class);
        final List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get("medewerkerId"), medewerkerId));
        query.where(builder.and(predicates.toArray(new Predicate[0])));
        query.orderBy(builder.asc(root.get("volgorde")));
        final TypedQuery<DashboardCardInstelling> emQuery = entityManager.createQuery(query);
        return emQuery.getResultList();
    }

    public void updateDashboardCards(final String medewerkerId, final List<DashboardCardInstelling> cards) {
        final List<DashboardCardInstelling> existingCards = listDashboardCards(medewerkerId);
        existingCards.forEach(existingCard -> {
            final Optional<DashboardCardInstelling> updatedCard = cards.stream()
                    .filter(card -> existingCard.getCardId().equals(card.getCardId()))
                    .findAny();
            if (updatedCard.isPresent()) {
                existingCard.setKolom(updatedCard.get().getKolom());
                existingCard.setVolgorde(updatedCard.get().getVolgorde());
                entityManager.persist(existingCard);
            } else {
                entityManager.remove(existingCard);
            }
        });
        cards.stream()
                .filter(card -> card.getId() == null)
                .forEach(newCard -> {
                    newCard.setMedewerkerId(medewerkerId);
                    entityManager.persist(newCard);
                });
    }

    public void addDashboardCard(final String medewerkerId, final DashboardCardInstelling card) {
        if (card.getSignaleringType() != null) {
            final SignaleringInstellingen instellingen = signaleringenService.readInstellingenUser(
                    card.getSignaleringType(), medewerkerId);
            instellingen.setDashboard(true);
            signaleringenService.createUpdateOrDeleteInstellingen(instellingen);
        }
        if (card.getId() == null) {
            card.setMedewerkerId(medewerkerId);
            entityManager.persist(card);
        }
    }

    public void deleteDashboardCard(final String medewerkerId, final DashboardCardInstelling card) {
        if (card.getSignaleringType() != null) {
            final SignaleringInstellingen instellingen = signaleringenService.readInstellingenUser(
                    card.getSignaleringType(), medewerkerId);
            instellingen.setDashboard(false);
            signaleringenService.createUpdateOrDeleteInstellingen(instellingen);
        }
        if (card.getId() != null) {
            entityManager.remove(entityManager.find(DashboardCardInstelling.class, card.getId()));
        }
    }
}
