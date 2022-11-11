package net.atos.zac.app.mailtemplate;

import net.atos.zac.app.admin.converter.RESTMailtemplateConverter;
import net.atos.zac.app.admin.model.RESTMailtemplate;
import net.atos.zac.mailtemplates.MailTemplateService;
import net.atos.zac.mailtemplates.model.Mail;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.util.UUID;

@Singleton
@Path("mailtemplates")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MailtemplateRESTService {

    @Inject
    private MailTemplateService mailTemplateService;

    @Inject
    private RESTMailtemplateConverter restMailtemplateConverter;

    @GET
    @Path("{mailtemplateEnum}/{zaakUUID}")
    public RESTMailtemplate findMailtemplate(@PathParam("mailtemplateEnum") final Mail mail,
            @PathParam("zaakUUID") final UUID zaakUUID) {
        // TODO Controleren adhv het zaakUUID of er een ander mailtemplate dan de default is ingesteld op het zaaktype.

        return restMailtemplateConverter.convert(mailTemplateService.findMailtemplate(mail));
    }
}
