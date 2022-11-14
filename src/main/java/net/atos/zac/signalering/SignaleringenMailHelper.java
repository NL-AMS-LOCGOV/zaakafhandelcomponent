/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.signalering;


import java.util.UUID;

import javax.inject.Inject;

import org.apache.commons.text.StringEscapeUtils;
import org.flowable.task.api.TaskInfo;

import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.configuratie.ConfiguratieService;
import net.atos.zac.flowable.CaseVariablesService;
import net.atos.zac.flowable.TaskService;
import net.atos.zac.identity.IdentityService;
import net.atos.zac.identity.model.Group;
import net.atos.zac.identity.model.User;
import net.atos.zac.mail.model.Ontvanger;
import net.atos.zac.signalering.model.Signalering;
import net.atos.zac.signalering.model.SignaleringSubject;
import net.atos.zac.signalering.model.SignaleringTarget;
import net.atos.zac.signalering.model.SignaleringType;

public class SignaleringenMailHelper {

    @Inject
    private ConfiguratieService configuratieService;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private DRCClientService drcClientService;

    @Inject
    private TaskService taskService;

    @Inject
    private CaseVariablesService caseVariablesService;

    @Inject
    private IdentityService identityService;

    public SignaleringTarget.Mail getTargetMail(final Signalering signalering) {
        switch (signalering.getTargettype()) {
            case GROUP -> {
                final Group group = identityService.readGroup(signalering.getTarget());
                if (group.getEmail() != null) {
                    return new SignaleringTarget.Mail(group.getName(), group.getEmail());
                }
            }
            case USER -> {
                final User user = identityService.readUser(signalering.getTarget());
                if (user.getEmail() != null) {
                    return new SignaleringTarget.Mail(user.getFullName(), user.getEmail());
                }
            }
        }
        return null;
    }

    public SignaleringSubject.Link getSubjectLink(final Signalering signalering) {
        switch (signalering.getSubjecttype()) {
            case ZAAK -> {
                final UUID uuid = UUID.fromString(signalering.getSubject());
                final Zaak zaak = zrcClientService.readZaak(uuid);
                final Zaaktype zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype());
                return new SignaleringSubject.Link(zaak.getIdentificatie(),
                                                   String.format("de zaak %s (%s)", zaak.getIdentificatie(), zaaktype.getOmschrijving()),
                                                   configuratieService.zaakTonenUrl(zaak.getIdentificatie()));
            }
            case TAAK -> {
                final String id = signalering.getSubject();
                final TaskInfo taak = taskService.readTask(id);
                final String zaakIdentificatie = caseVariablesService.readZaakIdentificatie(taak.getScopeId());
                final String zaaktypeOmschrijving = caseVariablesService.readZaaktypeOmschrijving(taak.getScopeId());
                return new SignaleringSubject.Link(taak.getName(),
                                                   String.format("de taak %s voor zaak %s (%s)", taak.getName(), zaakIdentificatie, zaaktypeOmschrijving),
                                                   configuratieService.taakTonenUrl(id));
            }
            case INFORMATIEOBJECT -> {
                final UUID uuid = UUID.fromString(signalering.getSubject());
                final EnkelvoudigInformatieobject informatieobject = drcClientService.readEnkelvoudigInformatieobject(uuid);
                return new SignaleringSubject.Link(informatieobject.getTitel(),
                                                   String.format("het document %s", informatieobject.getTitel()),
                                                   configuratieService.informatieobjectTonenUrl(uuid));
            }
        }
        return null;
    }

    public Ontvanger formatTo(final SignaleringTarget.Mail mail) {
        return new Ontvanger(mail.emailadres, mail.naam);
    }
}
