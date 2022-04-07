/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.signalering;

import static net.atos.zac.util.ValidationUtil.valideerObject;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.flowable.idm.api.Group;

import net.atos.zac.authentication.Medewerker;
import net.atos.zac.event.EventingService;
import net.atos.zac.signalering.model.Signalering;
import net.atos.zac.signalering.model.SignaleringInstellingen;
import net.atos.zac.signalering.model.SignaleringInstellingenZoekParameters;
import net.atos.zac.signalering.model.SignaleringTarget;
import net.atos.zac.signalering.model.SignaleringType;
import net.atos.zac.signalering.model.SignaleringZoekParameters;
import net.atos.zac.websocket.event.ScreenEventType;

@ApplicationScoped
@Transactional
public class SignaleringenService {


    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;

    @Inject
    private EventingService eventingService;

    private SignaleringType signaleringTypeInstance(final SignaleringType.Type signaleringsType) {
        return entityManager.find(SignaleringType.class, signaleringsType.toString());
    }

    /**
     * Factory method for constructing Signalering instances.
     *
     * @param signaleringsType the type of the signalering to construct
     * @return the constructed instance (subject and target are still null, type and tijdstip have been set)
     */
    public Signalering signaleringInstance(final SignaleringType.Type signaleringsType) {
        final Signalering instance = new Signalering();
        instance.setType(signaleringTypeInstance(signaleringsType));
        instance.setTijdstip(ZonedDateTime.now());
        return instance;
    }

    /**
     * Factory method for constructing SignaleringInstellingen instances.
     *
     * @param signaleringsType the signalering type of the instellingen to construct
     * @param ownerType        the type of the owner of the instellingen to construct
     * @param ownerId          the id of the owner of the instellingen to construct
     * @return the constructed instance (subject and target are still null, type and tijdstip have been set)
     */
    public SignaleringInstellingen signaleringInstellingenInstance(final SignaleringType.Type signaleringsType, final SignaleringTarget ownerType,
            final String ownerId) {
        return new SignaleringInstellingen(signaleringTypeInstance(signaleringsType), ownerType, ownerId);
    }

    /**
     * Business logic for deciding if signalling is necessary. Groep-targets will always get signalled but Medewerker-targets only when they are not themselves
     * the actor that caused the event (or when the actor is unknown).
     *
     * @param signalering the signalering (should have the target set)
     * @param actor       the actor (a gebruikersnaam) or null if unknown
     * @return true if signalling is necessary
     */
    public boolean isNecessary(final Signalering signalering, final String actor) {
        return signalering.getTargettype() != SignaleringTarget.MEDEWERKER || !signalering.getTarget().equals(actor);
    }

    public Signalering createSignalering(final Signalering signalering) {
        valideerObject(signalering);
        final Signalering created = entityManager.merge(signalering);
        eventingService.send(ScreenEventType.SIGNALERINGEN.updated(created));
        return created;
    }

    public void deleteSignalering(final Signalering signalering) {
        eventingService.send(ScreenEventType.SIGNALERINGEN.updated(signalering));
        entityManager.remove(signalering);
    }

    public void deleteSignaleringen(final SignaleringZoekParameters parameters) {
        findSignaleringen(parameters).forEach(this::deleteSignalering);
    }

    public Signalering readSignalering(final long id) {
        return entityManager.find(Signalering.class, id);
    }

    public List<Signalering> findSignaleringen(final SignaleringZoekParameters parameters) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Signalering> query = builder.createQuery(Signalering.class);
        final Root<Signalering> root = query.from(Signalering.class);
        return entityManager.createQuery(
                        query.select(root)
                                .where(getSignaleringWhere(parameters, builder, root))
                                .orderBy(builder.desc(root.get("tijdstip"))))
                .getResultList();
    }

    public ZonedDateTime latestSignalering(final SignaleringZoekParameters parameters) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<ZonedDateTime> query = builder.createQuery(ZonedDateTime.class);
        final Root<Signalering> root = query.from(Signalering.class);


        query.select(root.get("tijdstip"))
                .where(getSignaleringWhere(parameters, builder, root))
                .orderBy(builder.desc(root.get("tijdstip")));

        final List<ZonedDateTime> resultList = entityManager.createQuery(query).getResultList();

        if (resultList != null && !resultList.isEmpty()) {
            return resultList.get(0);
        } else {
            return null;
        }
    }

    private Predicate getSignaleringWhere(final SignaleringZoekParameters parameters, final CriteriaBuilder builder, final Root<Signalering> root) {
        final List<Predicate> where = new ArrayList<>();
        where.add(builder.equal(root.get("targettype"), parameters.getTargettype()));
        if (parameters.getTarget() != null) {
            where.add(builder.equal(root.get("target"), parameters.getTarget()));
        }
        if (!parameters.getTypes().isEmpty()) {
            where.add(root.get("type").get("id").in(parameters.getTypes().stream().map(Enum::toString).collect(Collectors.toList())));
        }
        if (parameters.getSubjecttype() != null) {
            where.add(builder.equal(root.get("type").get("subjecttype"), parameters.getSubjecttype()));
            if (parameters.getSubject() != null) {
                where.add(builder.equal(root.get("subject"), parameters.getSubject()));
            }
        }
        return builder.and(where.toArray(new Predicate[0]));
    }

    public SignaleringInstellingen createUpdateOrDeleteInstellingen(final SignaleringInstellingen instellingen) {
        valideerObject(instellingen);
        if (instellingen.isEmpty()) {
            if (instellingen.getId() != null) {
                entityManager.remove(entityManager.find(SignaleringInstellingen.class, instellingen.getId()));
            }
            return null;
        }
        return entityManager.merge(instellingen);
    }

    public SignaleringInstellingen readInstellingen(final SignaleringType.Type type, final Group groep) {
        final Signalering signalering = signaleringInstance(type);
        signalering.setTarget(groep);
        return readInstellingen(signalering);
    }

    public SignaleringInstellingen readInstellingen(final SignaleringType.Type type, final Medewerker medewerker) {
        final Signalering signalering = signaleringInstance(type);
        signalering.setTarget(medewerker);
        return readInstellingen(signalering);
    }

    public SignaleringInstellingen readInstellingen(final Signalering signalering) {
        final List<SignaleringInstellingen> instellingen = findInstellingen(new SignaleringInstellingenZoekParameters(signalering));
        if (instellingen.size() == 1) {
            return instellingen.get(0);
        }
        return new SignaleringInstellingen(signalering.getType(), signalering.getTargettype(), signalering.getTarget());
    }

    public List<SignaleringInstellingen> findInstellingen(final SignaleringInstellingenZoekParameters parameters) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<SignaleringInstellingen> query = builder.createQuery(SignaleringInstellingen.class);
        final Root<SignaleringInstellingen> root = query.from(SignaleringInstellingen.class);
        return entityManager.createQuery(
                        query.select(root)
                                .where(getSignaleringInstellingenWhere(parameters, builder, root)))
                .getResultList();
    }

    private Predicate getSignaleringInstellingenWhere(final SignaleringInstellingenZoekParameters parameters, final CriteriaBuilder builder,
            final Root<SignaleringInstellingen> root) {
        final List<Predicate> where = new ArrayList<>();
        if (parameters.getOwner() != null) {
            switch (parameters.getOwnertype()) {
                case GROEP -> {
                    where.add(builder.equal(root.get("groep"), parameters.getOwner()));
                }
                case MEDEWERKER -> {
                    where.add(builder.equal(root.get("medewerker"), parameters.getOwner()));
                }
            }
        }
        if (parameters.getType() != null) {
            where.add(builder.equal(root.get("type").get("id"), parameters.getType().toString()));
        }
        return builder.and(where.toArray(new Predicate[0]));
    }

    public List<SignaleringInstellingen> listInstellingen(final SignaleringInstellingenZoekParameters parameters) {
        final Map<SignaleringType.Type, SignaleringInstellingen> map = findInstellingen(parameters).stream()
                .collect(Collectors.toMap(instellingen -> instellingen.getType().getType(), Function.identity()));
        Arrays.stream(SignaleringType.Type.values())
                .filter(type -> !map.containsKey(type))
                .forEach(type -> map.put(type, signaleringInstellingenInstance(type, parameters.getOwnertype(), parameters.getOwner())));
        return map.values().stream()
                .sorted(Comparator.comparing(SignaleringInstellingen::getType))
                .toList();
    }

    public int count() {
        return SignaleringType.Type.values().length;
    }
}
