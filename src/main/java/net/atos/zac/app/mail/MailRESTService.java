/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.mail;

import static net.atos.zac.policy.PolicyService.assertPolicy;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mailjet.client.errors.MailjetException;

import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Statustype;
import net.atos.zac.app.mail.converter.RESTMailGegevensConverter;
import net.atos.zac.app.mail.model.RESTMailGegevens;
import net.atos.zac.flowable.CaseVariablesService;
import net.atos.zac.mail.MailService;
import net.atos.zac.mail.model.Bronnen;
import net.atos.zac.policy.PolicyService;
import net.atos.zac.util.ValidationUtil;

@Singleton
@Path("mail")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MailRESTService {

    @Inject
    private MailService mailService;

    @Inject
    private CaseVariablesService caseVariablesService;

    @Inject
    private PolicyService policyService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private RESTMailGegevensConverter restMailGegevensConverter;

    @POST
    @Path("send/{zaakUuid}")
    public void sendMail(@PathParam("zaakUuid") final UUID zaakUUID,
            final RESTMailGegevens restMailGegevens) throws MailjetException {
        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        assertPolicy(policyService.readZaakRechten(zaak).getBehandelen());
        if (!ValidationUtil.isValidEmail(restMailGegevens.ontvanger)) {
            throw new RuntimeException(String.format("email '%s' is not valid", restMailGegevens.ontvanger));
        }
        mailService.sendMail(
                restMailGegevensConverter.convert(restMailGegevens), Bronnen.fromZaak(zaak));
    }

    @POST
    @Path("acknowledge/{zaakUuid}")
    public void sendAcknowledgmentReceiptMail(@PathParam("zaakUuid") final UUID zaakUuid,
            final RESTMailGegevens restMailGegevens) throws MailjetException {
        final Zaak zaak = zrcClientService.readZaak(zaakUuid);
        assertPolicy(!caseVariablesService.findOntvangstbevestigingVerstuurd(zaak.getUuid()).orElse(false) &&
                             policyService.readZaakRechten(zaak).getBehandelen());
        if (!ValidationUtil.isValidEmail(restMailGegevens.ontvanger)) {
            throw new RuntimeException(String.format("email '%s' is not valid", restMailGegevens.ontvanger));
        }
        mailService.sendMail(
                restMailGegevensConverter.convert(restMailGegevens), Bronnen.fromZaak(zaak));

        final Statustype statustype = zaak.getStatus() != null ?
                ztcClientService.readStatustype(zrcClientService.readStatus(zaak.getStatus()).getStatustype()) : null;
        if (!Statustype.isHeropend(statustype)) {
            caseVariablesService.setOntvangstbevestigingVerstuurd(zaakUuid, Boolean.TRUE);
        }
    }
}
