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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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

import org.flowable.task.api.TaskInfo;

import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.event.EventingService;
import net.atos.zac.flowable.TakenService;
import net.atos.zac.mail.MailService;
import net.atos.zac.mail.model.Bronnen;
import net.atos.zac.mail.model.MailAdres;
import net.atos.zac.mailtemplates.MailTemplateService;
import net.atos.zac.mailtemplates.model.Mail;
import net.atos.zac.mailtemplates.model.MailGegevens;
import net.atos.zac.mailtemplates.model.MailTemplate;
import net.atos.zac.signalering.model.Signalering;
import net.atos.zac.signalering.model.SignaleringDetail;
import net.atos.zac.signalering.model.SignaleringInstellingen;
import net.atos.zac.signalering.model.SignaleringInstellingenZoekParameters;
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
    private MailTemplateService mailTemplateService;

    @Inject
    private SignaleringenMailHelper signaleringenMailHelper;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private TakenService takenService;

    @Inject
    private DRCClientService drcClientService;

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
    public SignaleringInstellingen signaleringInstellingenInstance(final SignaleringType.Type signaleringsType,
            final SignaleringTarget ownerType,
            final String ownerId) {
        return new SignaleringInstellingen(signaleringTypeInstance(signaleringsType), ownerType, ownerId);
    }

    /**
     * Factory method for constructing SignaleringVerzonden instances.
     *
     * @param signalering the signalering that has been sent
     * @return the constructed instance (all members have been set)
     */
    public SignaleringVerzonden signaleringVerzondenInstance(final Signalering signalering) {
        final SignaleringVerzonden instance = new SignaleringVerzonden();
        instance.setTijdstip(ZonedDateTime.now());
        instance.setType(signaleringTypeInstance(signalering.getType().getType()));
        instance.setTargettype(signalering.getTargettype());
        instance.setTarget(signalering.getTarget());
        instance.setSubject(signalering.getSubject());
        instance.setDetail(signalering.getDetail());
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

    public void deleteSignaleringen(final SignaleringZoekParameters parameters) {
        final Map<String, Signalering> removed = new HashMap<>();
        listSignaleringen(parameters)
                .forEach(signalering -> {
                    removed.put(signalering.getTarget() + ';' + signalering.getType().getType(), signalering);
                    entityManager.remove(signalering);
                });
        removed.values()
                .forEach(signalering -> eventingService.send(ScreenEventType.SIGNALERINGEN.updated(signalering)));
    }

    public List<Signalering> listSignaleringen(final SignaleringZoekParameters parameters) {
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

    private Predicate getSignaleringWhere(final SignaleringZoekParameters parameters, final CriteriaBuilder builder,
            final Root<Signalering> root) {
        final List<Predicate> where = new ArrayList<>();
        where.add(builder.equal(root.get("targettype"), parameters.getTargettype()));
        if (parameters.getTarget() != null) {
            where.add(builder.equal(root.get("target"), parameters.getTarget()));
        }
        if (!parameters.getTypes().isEmpty()) {
            where.add(root.get("type").get("id")
                              .in(parameters.getTypes().stream().map(Enum::toString).collect(Collectors.toList())));
        }
        if (parameters.getSubjecttype() != null) {
            where.add(builder.equal(root.get("type").get("subjecttype"), parameters.getSubjecttype()));
            if (parameters.getSubject() != null) {
                where.add(builder.equal(root.get("subject"), parameters.getSubject()));
            }
        }
        return builder.and(where.toArray(new Predicate[0]));
    }

    public void sendSignalering(final Signalering signalering) {
        valideerObject(signalering);
        final SignaleringTarget.Mail mail = signaleringenMailHelper.getTargetMail(signalering);
        if (mail != null) {
            final MailAdres from = mailService.getGemeenteMailAdres();
            final MailAdres to = signaleringenMailHelper.formatTo(mail);
            final MailTemplate mailTemplate = getMailtemplate(signalering);
            final Bronnen.Builder bronnenBuilder = new Bronnen.Builder();
            switch (signalering.getSubjecttype()) {
                case ZAAK -> {
                    bronnenBuilder.add(getZaak(signalering.getSubject()));
                    if (signalering.getType().getType() == SignaleringType.Type.ZAAK_DOCUMENT_TOEGEVOEGD) {
                        bronnenBuilder.add(getDocument(signalering.getDetail()));
                    }
                }
                case TAAK -> bronnenBuilder.add(getTaak(signalering.getSubject()));
                case DOCUMENT -> bronnenBuilder.add(getDocument(signalering.getSubject()));
            }
            mailService.sendMail(new MailGegevens(from, to, mailTemplate.getOnderwerp(), mailTemplate.getBody()),
                                 bronnenBuilder.build());
        }
    }

    private Zaak getZaak(final String zaakUUID) {
        return zrcClientService.readZaak(UUID.fromString(zaakUUID));
    }

    private TaskInfo getTaak(final String taakID) {
        return takenService.readTask(taakID);
    }

    private EnkelvoudigInformatieobject getDocument(final String documentUUID) {
        return drcClientService.readEnkelvoudigInformatieobject(UUID.fromString(documentUUID));
    }

    private MailTemplate getMailtemplate(final Signalering signalering) {
        return mailTemplateService.readMailtemplate(
                switch (signalering.getType().getType()) {
                    case TAAK_OP_NAAM -> Mail.SIGNALERING_TAAK_OP_NAAM;
                    case TAAK_VERLOPEN -> Mail.SIGNALERING_TAAK_VERLOPEN;
                    case ZAAK_DOCUMENT_TOEGEVOEGD -> Mail.SIGNALERING_ZAAK_DOCUMENT_TOEGEVOEGD;
                    case ZAAK_OP_NAAM -> Mail.SIGNALERING_ZAAK_OP_NAAM;
                    case ZAAK_VERLOPEND -> switch (SignaleringDetail.valueOf(signalering.getDetail())) {
                        case STREEFDATUM -> Mail.SIGNALERING_ZAAK_VERLOPEND_STREEFDATUM;
                        case FATALE_DATUM -> Mail.SIGNALERING_ZAAK_VERLOPEND_FATALE_DATUM;
                    };
                });
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
        final List<SignaleringInstellingen> instellingen = listInstellingen(
                new SignaleringInstellingenZoekParameters(signalering));
        if (instellingen.size() == 1) {
            return instellingen.get(0);
        }
        return new SignaleringInstellingen(signalering.getType(), signalering.getTargettype(), signalering.getTarget());
    }

    public List<SignaleringInstellingen> listInstellingen(final SignaleringInstellingenZoekParameters parameters) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<SignaleringInstellingen> query = builder.createQuery(SignaleringInstellingen.class);
        final Root<SignaleringInstellingen> root = query.from(SignaleringInstellingen.class);
        return entityManager.createQuery(
                        query.select(root)
                                .where(getSignaleringInstellingenWhere(parameters, builder, root)))
                .getResultList();
    }

    private Predicate getSignaleringInstellingenWhere(final SignaleringInstellingenZoekParameters parameters,
            final CriteriaBuilder builder,
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

    public List<SignaleringInstellingen> listInstellingenInclusiefMogelijke(
            final SignaleringInstellingenZoekParameters parameters) {
        final Map<SignaleringType.Type, SignaleringInstellingen> map = listInstellingen(parameters).stream()
                .collect(Collectors.toMap(instellingen -> instellingen.getType().getType(), Function.identity()));
        Arrays.stream(SignaleringType.Type.values())
                .filter(type -> type.isTarget(parameters.getOwnertype()))
                .filter(type -> !map.containsKey(type))
                .forEach(type -> map.put(type, signaleringInstellingenInstance(type, parameters.getOwnertype(),
                                                                               parameters.getOwner())));
        return map.values().stream()
                .sorted(Comparator.comparing(SignaleringInstellingen::getType))
                .toList();
    }

    public int count() {
        return SignaleringType.Type.values().length;
    }

    public SignaleringVerzonden createSignaleringVerzonden(final Signalering signalering) {
        final SignaleringVerzonden signaleringVerzonden = signaleringVerzondenInstance(signalering);
        valideerObject(signaleringVerzonden);
        return entityManager.merge(signaleringVerzonden);
    }

    public void deleteSignaleringVerzonden(final SignaleringVerzondenZoekParameters verzonden) {
        findSignaleringVerzonden(verzonden).ifPresent(entityManager::remove);
    }

    public Optional<SignaleringVerzonden> findSignaleringVerzonden(
            final SignaleringVerzondenZoekParameters parameters) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<SignaleringVerzonden> query = builder.createQuery(SignaleringVerzonden.class);
        final Root<SignaleringVerzonden> root = query.from(SignaleringVerzonden.class);
        final List<SignaleringVerzonden> result = entityManager.createQuery(
                        query.select(root)
                                .where(getSignaleringVerzondenWhere(parameters, builder, root)))
                .getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    private Predicate getSignaleringVerzondenWhere(final SignaleringVerzondenZoekParameters parameters,
            final CriteriaBuilder builder,
            final Root<SignaleringVerzonden> root) {
        final List<Predicate> where = new ArrayList<>();
        where.add(builder.equal(root.get("targettype"), parameters.getTargettype()));
        if (parameters.getTarget() != null) {
            where.add(builder.equal(root.get("target"), parameters.getTarget()));
        }
        if (!parameters.getTypes().isEmpty()) {
            where.add(root.get("type").get("id")
                              .in(parameters.getTypes().stream().map(Enum::toString).collect(Collectors.toList())));
        }
        if (parameters.getSubjecttype() != null) {
            where.add(builder.equal(root.get("type").get("subjecttype"), parameters.getSubjecttype()));
            if (parameters.getSubject() != null) {
                where.add(builder.equal(root.get("subject"), parameters.getSubject()));
            }
        }
        if (parameters.getDetail() != null) {
            where.add(builder.equal(root.get("detail"), parameters.getDetail().toString()));
        }
        return builder.and(where.toArray(new Predicate[0]));
    }
}
