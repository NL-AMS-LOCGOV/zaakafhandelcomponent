/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.signalering.event;

import java.net.URI;
import java.util.logging.Logger;

import javax.annotation.ManagedBean;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

import org.flowable.task.api.TaskInfo;

import net.atos.client.zgw.shared.util.URIUtil;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Rol;
import net.atos.client.zgw.zrc.model.RolMedewerker;
import net.atos.client.zgw.zrc.model.RolOrganisatorischeEenheid;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.AardVanRol;
import net.atos.zac.event.AbstractEventObserver;
import net.atos.zac.flowable.FlowableHelper;
import net.atos.zac.flowable.FlowableService;
import net.atos.zac.signalering.SignaleringenService;
import net.atos.zac.signalering.model.Signalering;

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
    private FlowableHelper flowableHelper;

    @Inject
    private SignaleringenService signaleringenService;

    @Override
    public void onFire(final @ObservesAsync SignaleringEvent<?> event) {
        final Signalering signalering = buildSignalering(
                signaleringenService.signaleringInstance(event.getObjectType()), event);

        if (signalering != null && signaleringenService.isSubcribedTo(signalering)) {
            signaleringenService.createSignalering(signalering);
        }
    }

    private Signalering buildSignalering(final Signalering signalering, final SignaleringEvent<?> event) {
        switch (event.getObjectType()) {
            case ZAAK_OP_NAAM -> {
                final Rol<?> rol = zrcClientService.readRol((URI) event.getObjectId());
                final Zaak zaak = zrcClientService.readZaak(rol.getZaak());
                final URI roltype = ztcClientService.readRoltype(zaak.getZaaktype(), AardVanRol.BEHANDELAAR).getUrl();
                if (URIUtil.equals(roltype, rol.getRoltype())) {
                    signalering.setSubject(zaak);
                    return addTarget(signalering, rol);
                }
            }
            case TAAK_OP_NAAM -> {
                final TaskInfo taskInfo = flowableService.readTask((String) event.getObjectId());
                if (taskInfo.getAssignee() != null) {
                    signalering.setSubject(taskInfo);
                    signalering.setTarget(
                            flowableHelper.createMedewerker(taskInfo.getAssignee()));
                    return signalering;
                }
            }
            default -> LOG.warning(String.format("unknown SignaleringType %s", event.getObjectType()));
        }
        return null;
    }

    private Signalering addTarget(final Signalering signalering, final Rol<?> rol) {
        switch (rol.getBetrokkeneType()) {
            case MEDEWERKER -> {
                final RolMedewerker rolMedewerker = (RolMedewerker) rol;
                signalering.setTarget(
                        flowableHelper.createMedewerker(rolMedewerker.getBetrokkeneIdentificatie().getIdentificatie()));
                return signalering;
            }
            case ORGANISATORISCHE_EENHEID -> {
                final RolOrganisatorischeEenheid rolGroep = (RolOrganisatorischeEenheid) rol;
                signalering.setTarget(
                        flowableService.readGroup(rolGroep.getBetrokkeneIdentificatie().getIdentificatie()));
                return signalering;
            }
            default -> LOG.warning(String.format("unexpected BetrokkeneType %s", rol.getBetrokkeneType()));
        }
        return null;
    }
}
