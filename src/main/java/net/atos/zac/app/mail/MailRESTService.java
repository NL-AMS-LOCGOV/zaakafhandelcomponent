/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.mail;

import static net.atos.zac.policy.PolicyService.assertActie;

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

import net.atos.zac.app.mail.model.RESTMailObject;
import net.atos.zac.flowable.CaseVariablesService;
import net.atos.zac.mail.MailService;
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

    @POST
    @Path("send/{zaakUuid}")
    public void sendMail(@PathParam("zaakUuid") final UUID zaakUuid, final RESTMailObject restMailObject) throws MailjetException {
        assertActie(policyService.readZaakActies(zaakUuid).getVersturenEmail());
        if (!ValidationUtil.isValidEmail(restMailObject.ontvanger)) {
            throw new RuntimeException(String.format("email '%s' is not valid", restMailObject.ontvanger));
        }
        mailService.sendMail(restMailObject.ontvanger, restMailObject.onderwerp,
                             restMailObject.body, restMailObject.createDocumentFromMail, zaakUuid);
    }

    @POST
    @Path("acknowledge/{zaakUuid}")
    public void sendAcknowledgmentReceiptMail(@PathParam("zaakUuid") final UUID zaakUuid, final RESTMailObject restMailObject) throws MailjetException {
        assertActie(policyService.readZaakActies(zaakUuid).getVersturenOntvangstbevestiging());
        if (!ValidationUtil.isValidEmail(restMailObject.ontvanger)) {
            throw new RuntimeException(String.format("email '%s' is not valid", restMailObject.ontvanger));
        }
        mailService.sendMail(restMailObject.ontvanger, restMailObject.onderwerp, restMailObject.body, restMailObject.createDocumentFromMail, zaakUuid);
        caseVariablesService.setOntvangstbevestigingVerstuurd(zaakUuid, Boolean.TRUE);
    }
}
