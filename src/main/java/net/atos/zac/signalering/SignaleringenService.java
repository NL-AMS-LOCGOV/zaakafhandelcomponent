/*
 * SPDX-FileCopyrightText: 2022 Atos
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
import java.util.logging.Logger;
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

import net.atos.zac.event.EventingService;
import net.atos.zac.mail.MailService;
import net.atos.zac.mail.model.Ontvanger;
import net.atos.zac.signalering.model.Signalering;
import net.atos.zac.signalering.model.SignaleringInstellingen;
import net.atos.zac.signalering.model.SignaleringInstellingenZoekParameters;
import net.atos.zac.signalering.model.SignaleringSubject;
import net.atos.zac.signalering.model.SignaleringSubjectField;
import net.atos.zac.signalering.model.SignaleringTarget;
import net.atos.zac.signalering.model.SignaleringType;
import net.atos.zac.signalering.model.SignaleringVerzonden;
import net.atos.zac.signalering.model.SignaleringVerzondenZoekParameters;
import net.atos.zac.signalering.model.SignaleringZoekParameters;
import net.atos.zac.websocket.event.ScreenEventType;

@ApplicationScoped
@Transactional
public class SignaleringenService {
    private static final Logger LOG = Logger.getLogger(SignaleringenService.class.getName());

    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;

    @Inject
    private EventingService eventingService;

    @Inject
    private MailService mailService;

    @Inject
    private SignaleringenMailHelper signaleringenMailHelper;

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
        instance.setTijdstip(ZonedDateTime.now());
        instance.setType(signaleringTypeInstance(signaleringsType));
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
     * Factory method for constructing SignaleringVerzonden instances.
     *
     * @param signalering the signalering that has been sent
     * @param field       the field the signalering has been sent for
     * @return the constructed instance (all members have been set)
     */
    public SignaleringVerzonden signaleringVerzondenInstance(final Signalering signalering, final SignaleringSubjectField field) {
        final SignaleringVerzonden instance = new SignaleringVerzonden();
        instance.setTijdstip(ZonedDateTime.now());
        instance.setType(signaleringTypeInstance(signalering.getType().getType()));
        instance.setTargettype(signalering.getTargettype());
        instance.setTarget(signalering.getTarget());
        instance.setSubject(signalering.getSubject());
        instance.setSubjectfield(field);
        return instance;
    }

    /**
     * Business logic for deciding if signalling is necessary. Groep-targets will always get signalled but user-targets only when they are not themselves
     * the actor that caused the event (or when the actor is unknown).
     *
     * @param signalering the signalering (should have the target set)
     * @param actor       the actor (a gebruikersnaam) or null if unknown
     * @return true if signalling is necessary
     */
    public boolean isNecessary(final Signalering signalering, final String actor) {
        return signalering.getTargettype() != SignaleringTarget.USER || !signalering.getTarget().equals(actor);
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

    public void sendSignalering(final Signalering signalering, final String bericht) {
        valideerObject(signalering);
        final SignaleringTarget.Mail mail = signaleringenMailHelper.getTargetMail(signalering);
        if (mail != null) {
            final Ontvanger to = signaleringenMailHelper.formatTo(mail);
            final SignaleringType.Type type = signalering.getType().getType();
            final SignaleringSubject.Link link = signaleringenMailHelper.getSubjectLink(signalering);
            final String subject = signaleringenMailHelper.formatSubject(type, link);
            final String body = signaleringenMailHelper.formatBody(type, mail, link, bericht);
            mailService.sendMail(to, subject, body);
        }
    }

    public void sendSignalering(final Signalering signalering) {
        sendSignalering(signalering, null);
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

    public SignaleringInstellingen readInstellingenGroup(final SignaleringType.Type type, final String target) {
        final Signalering signalering = signaleringInstance(type);
        signalering.setTargetGroup(target);
        return readInstellingen(signalering);
    }

    public SignaleringInstellingen readInstellingenUser(final SignaleringType.Type type, final String target) {
        final Signalering signalering = signaleringInstance(type);
        signalering.setTargetUser(target);
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
                case GROUP -> {
                    where.add(builder.equal(root.get("groep"), parameters.getOwner()));
                }
                case USER -> {
                    where.add(builder.equal(root.get("medewerker"), parameters.getOwner()));
                }
            }
        }
        if (parameters.getType() != null) {
            where.add(builder.equal(root.get("type").get("id"), parameters.getType().toString()));
        }
        if (parameters.getDashboard()) {
            where.add(builder.isTrue(root.get("dashboard")));
        }
        if (parameters.getMail()) {
            where.add(builder.isTrue(root.get("mail")));
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

    public SignaleringVerzonden createSignaleringVerzonden(final Signalering signalering, final SignaleringSubjectField field) {
        final SignaleringVerzonden signaleringVerzonden = signaleringVerzondenInstance(signalering, field);
        valideerObject(signaleringVerzonden);
        return entityManager.merge(signaleringVerzonden);
    }

    public void deleteSignaleringVerzonden(final SignaleringVerzondenZoekParameters verzonden) {
        final SignaleringVerzonden signaleringVerzonden = findSignaleringVerzonden(verzonden);
        if (signaleringVerzonden != null) {
            entityManager.remove(signaleringVerzonden);
        }
    }

    public SignaleringVerzonden findSignaleringVerzonden(final SignaleringVerzondenZoekParameters parameters) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<SignaleringVerzonden> query = builder.createQuery(SignaleringVerzonden.class);
        final Root<SignaleringVerzonden> root = query.from(SignaleringVerzonden.class);
        final List<SignaleringVerzonden> result = entityManager.createQuery(
                        query.select(root)
                                .where(getSignaleringVerzondenWhere(parameters, builder, root)))
                .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    private Predicate getSignaleringVerzondenWhere(final SignaleringVerzondenZoekParameters parameters, final CriteriaBuilder builder,
            final Root<SignaleringVerzonden> root) {
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
        if (parameters.getSubjectfield() != null) {
            where.add(builder.equal(root.get("subjectfield"), parameters.getSubjectfield()));
        }
        return builder.and(where.toArray(new Predicate[0]));
    }
}
