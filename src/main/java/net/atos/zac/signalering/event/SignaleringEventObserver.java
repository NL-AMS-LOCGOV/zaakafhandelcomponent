/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.signalering.event;

import java.net.URI;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.ManagedBean;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

import org.flowable.task.api.TaskInfo;

import net.atos.client.zgw.shared.util.URIUtil;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.BetrokkeneType;
import net.atos.client.zgw.zrc.model.Rol;
import net.atos.client.zgw.zrc.model.RolListParameters;
import net.atos.client.zgw.zrc.model.RolMedewerker;
import net.atos.client.zgw.zrc.model.RolOrganisatorischeEenheid;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.AardVanRol;
import net.atos.client.zgw.ztc.model.Roltype;
import net.atos.zac.event.AbstractEventObserver;
import net.atos.zac.flowable.TaskService;
import net.atos.zac.identity.IdentityService;
import net.atos.zac.signalering.SignaleringenService;
import net.atos.zac.signalering.model.Signalering;
import net.atos.zac.signalering.model.SignaleringInstellingen;

/**
 * This bean listens for SignaleringEvents and handles them.
 */
@ManagedBean
public class SignaleringEventObserver extends AbstractEventObserver<SignaleringEvent<?>> {

    private static final Logger LOG = Logger.getLogger(SignaleringEventObserver.class.getName());

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private TaskService taskService;

    @Inject
    private IdentityService identityService;

    @Inject
    private SignaleringenService signaleringenService;

    @Override
    public void onFire(final @ObservesAsync SignaleringEvent<?> event) {
        LOG.fine(() -> String.format("Signalering event ontvangen: %s", event.toString()));
        try {
            final Signalering signalering = buildSignalering(event);
            if (signalering != null && signaleringenService.isNecessary(signalering, event.getActor())) {
                final SignaleringInstellingen subscriptions = signaleringenService.readInstellingen(signalering);
                if (subscriptions.isDashboard()) {
                    signaleringenService.createSignalering(signalering);
                }
                if (subscriptions.isMail()) {
                    signaleringenService.sendSignalering(signalering);
                }
            }
        } catch (final Throwable ex) {
            LOG.log(Level.SEVERE, "asynchronous guard", ex);
        }
    }

    private Signalering getInstance(final SignaleringEvent<?> event) {
        return signaleringenService.signaleringInstance(event.getObjectType());
    }

    private Signalering getSignaleringVoorRol(final SignaleringEvent<?> event, final Zaak subject, final Rol<?> rol) {
        final Signalering signalering = getInstance(event);
        signalering.setSubject(subject);
        return addTarget(signalering, rol);
    }

    private Signalering getSignaleringVoorMedewerker(final SignaleringEvent<?> event, final Zaak subject,
            final RolMedewerker rol) {
        return getSignaleringVoorRol(event, subject, rol);
    }

    private Signalering getSignaleringVoorGroup(final SignaleringEvent<?> event, final Zaak subject,
            final RolOrganisatorischeEenheid rol) {
        if (getRolBehandelaarMedewerker(subject).isEmpty()) {
            return getSignaleringVoorRol(event, subject, rol);
        }
        return null;
    }

    private Signalering getSignaleringVoorBehandelaar(final SignaleringEvent<?> event, final Zaak subject,
            final ZaakInformatieobject detail) {
        final Optional<Rol<?>> behandelaar = getRolBehandelaarMedewerker(subject);
        if (behandelaar.isPresent()) {
            final Signalering signalering = getSignaleringVoorRol(event, subject, behandelaar.get());
            signalering.setDetail(detail);
            return signalering;
        }
        return null;
    }

    private Signalering getSignaleringVoorBehandelaar(final SignaleringEvent<?> event, final TaskInfo subject) {
        if (subject.getAssignee() != null) {
            final Signalering signalering = getInstance(event);
            signalering.setSubject(subject);
            return addTarget(signalering, subject);
        }
        return null;
    }

    private Signalering buildSignalering(final SignaleringEvent<?> event) {
        switch (event.getObjectType()) {
            case ZAAK_DOCUMENT_TOEGEVOEGD -> {
                final Zaak subject = zrcClientService.readZaak((URI) event.getObjectId().getResource());
                final ZaakInformatieobject detail =
                        zrcClientService.readZaakinformatieobject(
                                URIUtil.parseUUIDFromResourceURI((URI) event.getObjectId().getDetail()));
                return getSignaleringVoorBehandelaar(event, subject, detail);
            }
            case ZAAK_OP_NAAM -> {
                final Rol<?> rol = zrcClientService.readRol((URI) event.getObjectId().getResource());
                if (AardVanRol.fromValue(rol.getOmschrijvingGeneriek()) == AardVanRol.BEHANDELAAR) {
                    final Zaak subject = zrcClientService.readZaak(rol.getZaak());
                    switch (rol.getBetrokkeneType()) {
                        case MEDEWERKER -> {
                            return getSignaleringVoorMedewerker(event, subject, (RolMedewerker) rol);
                        }
                        case ORGANISATORISCHE_EENHEID -> {
                            return getSignaleringVoorGroup(event, subject, (RolOrganisatorischeEenheid) rol);
                        }
                        default -> LOG.warning(String.format("unexpected BetrokkeneType %s", rol.getBetrokkeneType()));
                    }
                }
            }
            case TAAK_OP_NAAM -> {
                final TaskInfo subject = taskService.readOpenTask((String) event.getObjectId().getResource());
                return getSignaleringVoorBehandelaar(event, subject);
            }
            case ZAAK_VERLOPEND, TAAK_VERLOPEN -> {
                // These are NOT event driven and should not show up here
                LOG.warning(String.format("ignored SignaleringType %s", event.getObjectType()));
            }
        }
        return null;
    }

    private Roltype getRoltypeBehandelaar(final Zaak zaak) {
        return ztcClientService.readRoltype(AardVanRol.BEHANDELAAR, zaak.getZaaktype());
    }

    private Optional<Rol<?>> getRolBehandelaarMedewerker(final Zaak zaak) {
        return getRol(zaak, getRoltypeBehandelaar(zaak), BetrokkeneType.MEDEWERKER);
    }

    private Optional<Rol<?>> getRolBehandelaarGroup(final Zaak zaak) {
        return getRol(zaak, getRoltypeBehandelaar(zaak), BetrokkeneType.ORGANISATORISCHE_EENHEID);
    }

    private Optional<Rol<?>> getRol(final Zaak zaak, final Roltype roltype, final BetrokkeneType betrokkeneType) {
        return zrcClientService.listRollen(new RolListParameters(zaak.getUrl(), roltype.getUrl(), betrokkeneType))
                .getSingleResult();
    }

    private Signalering addTarget(final Signalering signalering, final Rol<?> rol) {
        switch (rol.getBetrokkeneType()) {
            case MEDEWERKER -> {
                final RolMedewerker rolMedewerker = (RolMedewerker) rol;
                return addTargetUser(signalering, rolMedewerker.getBetrokkeneIdentificatie().getIdentificatie());
            }
            case ORGANISATORISCHE_EENHEID -> {
                final RolOrganisatorischeEenheid rolGroep = (RolOrganisatorischeEenheid) rol;
                return addTargetGroup(signalering, rolGroep.getBetrokkeneIdentificatie().getIdentificatie());
            }
            default -> LOG.warning(String.format("unexpected BetrokkeneType %s", rol.getBetrokkeneType()));
        }
        return null;
    }

    private Signalering addTarget(final Signalering signalering, final TaskInfo taskInfo) {
        return addTargetUser(signalering, taskInfo.getAssignee());
    }

    private Signalering addTargetUser(final Signalering signalering, final String userId) {
        signalering.setTarget(identityService.readUser(userId));
        return signalering;
    }

    private Signalering addTargetGroup(final Signalering signalering, final String groupId) {
        signalering.setTarget(identityService.readGroup(groupId));
        return signalering;
    }
}
