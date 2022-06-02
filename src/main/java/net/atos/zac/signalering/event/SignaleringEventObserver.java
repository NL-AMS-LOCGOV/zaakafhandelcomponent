/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.signalering.event;

import java.net.URI;
import java.util.Optional;
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
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.AardVanRol;
import net.atos.client.zgw.ztc.model.Roltype;
import net.atos.zac.event.AbstractEventObserver;
import net.atos.zac.flowable.FlowableHelper;
import net.atos.zac.flowable.FlowableService;
import net.atos.zac.identity.IdentityService;
import net.atos.zac.signalering.SignaleringenService;
import net.atos.zac.signalering.model.Signalering;
import net.atos.zac.signalering.model.SignaleringInstellingen;

/**
 * Deze bean luistert naar CmmnUpdateEvents, en werkt daar vervolgens flowable mee bij.
 */
@ManagedBean
public class SignaleringEventObserver extends AbstractEventObserver<SignaleringEvent<?>> {

    private static final Logger LOG = Logger.getLogger(SignaleringEventObserver.class.getName());

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private FlowableService flowableService;

    @Inject
    private IdentityService identityService;

    @Inject
    private FlowableHelper flowableHelper;

    @Inject
    private SignaleringenService signaleringenService;

    @Override
    public void onFire(final @ObservesAsync SignaleringEvent<?> event) {
        final Signalering signalering = buildSignalering(signaleringenService.signaleringInstance(event.getObjectType()), event);
        if (signalering != null && signaleringenService.isNecessary(signalering, event.getActor())) {
            final SignaleringInstellingen subscriptions = signaleringenService.readInstellingen(signalering);
            if (subscriptions.isDashboard()) {
                signaleringenService.createSignalering(signalering);
            }
            if (subscriptions.isMail()) {
                signaleringenService.sendSignalering(signalering);
            }
        }
    }

    private Signalering buildSignalering(final Signalering signalering, final SignaleringEvent<?> event) {
        switch (event.getObjectType()) {
            case ZAAK_DOCUMENT_TOEGEVOEGD -> {
                final Zaak subject = zrcClientService.readZaak((URI) event.getObjectId());
                final Optional<Rol<?>> behandelaar = getRolBehandelaarMedewerker(subject);
                if (behandelaar.isPresent()) {
                    signalering.setSubject(subject);
                    return addTarget(signalering, behandelaar.get());
                }
            }
            case ZAAK_OP_NAAM -> {
                final Rol<?> rol = zrcClientService.readRol((URI) event.getObjectId());
                // ZAAK_OP_NAAM for groep targets also works but for now we only need ZAAK_OP_NAAM for behandelaar targets
                if (rol.getBetrokkeneType() == BetrokkeneType.MEDEWERKER) { // So ignore the event when the betrokkene is not a MEDEWERKER
                    final Zaak subject = zrcClientService.readZaak(rol.getZaak());
                    if (URIUtil.equals(rol.getRoltype(), getRoltypeBehandelaar(subject).getUrl())) {
                        signalering.setSubject(subject);
                        return addTarget(signalering, rol);
                    }
                }
            }
            case TAAK_OP_NAAM -> {
                final TaskInfo subject = flowableService.readOpenTask((String) event.getObjectId());
                if (subject.getAssignee() != null) {
                    signalering.setSubject(subject);
                    return addTarget(signalering, subject);
                }
            }
            case ZAAK_VERLOPEND, TAAK_VERLOPEN -> {
                // These are NOT event driven and should not show up here
                LOG.warning(String.format("ignored SignaleringType %s", event.getObjectType()));
            }
        }
        return null;
    }

    private Roltype getRoltypeBehandelaar(final Zaak zaak) {
        return ztcClientService.readRoltype(zaak.getZaaktype(), AardVanRol.BEHANDELAAR);
    }

    private Optional<Rol<?>> getRolBehandelaarMedewerker(final Zaak zaak) {
        return getRol(zaak, getRoltypeBehandelaar(zaak), BetrokkeneType.MEDEWERKER);
    }

    private Optional<Rol<?>> getRolBehandelaarGroup(final Zaak zaak) {
        return getRol(zaak, getRoltypeBehandelaar(zaak), BetrokkeneType.ORGANISATORISCHE_EENHEID);
    }

    private Optional<Rol<?>> getRol(final Zaak zaak, final Roltype roltype, final BetrokkeneType betrokkeneType) {
        return zrcClientService.listRollen(new RolListParameters(zaak.getUrl(), roltype.getUrl(), betrokkeneType)).getSingleResult();
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
