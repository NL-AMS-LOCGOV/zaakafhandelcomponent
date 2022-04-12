/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.mail;

import static net.atos.zac.flowable.FlowableService.VAR_CASE_ONTVANGSTBEVESTIGING_VERSTUURD;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;

import net.atos.zac.app.mail.model.RESTMailObject;
import net.atos.zac.flowable.FlowableService;
import net.atos.zac.mail.MailService;
import net.atos.zac.util.ValidationUtil;

@Singleton
@Path("mail")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MailRESTService {

    @Inject
    private MailService mailService;

    @Inject
    private FlowableService flowableService;

    @POST
    @Path("send/{zaakUuid}")
    public void sendMail(@PathParam("zaakUuid") final UUID zaakUuid,
            final RESTMailObject restMailObject) throws MailjetException {
        if (!ValidationUtil.isValidEmail(restMailObject.ontvanger)) {
            throw new RuntimeException(String.format("email '%s' is not valid", restMailObject.ontvanger));
        }

        mailService.sendMail(restMailObject.ontvanger, restMailObject.onderwerp,
                             restMailObject.body, restMailObject.createDocumentFromMail, zaakUuid);
    }

    @POST
    @Path("acknowledge/{zaakUuid}")
    public void sendAcknowledgmentReceiptMail(@PathParam("zaakUuid") final UUID zaakUuid,
            final RESTMailObject restMailObject) throws MailjetException {
        if (!ValidationUtil.isValidEmail(restMailObject.ontvanger)) {
            throw new RuntimeException(String.format("email '%s' is not valid", restMailObject.ontvanger));
        }

        mailService.sendMail(restMailObject.ontvanger, restMailObject.onderwerp, restMailObject.body,
                             restMailObject.createDocumentFromMail, zaakUuid);
        flowableService.createVariableForCase(zaakUuid, VAR_CASE_ONTVANGSTBEVESTIGING_VERSTUURD, Boolean.TRUE);
    }
}
