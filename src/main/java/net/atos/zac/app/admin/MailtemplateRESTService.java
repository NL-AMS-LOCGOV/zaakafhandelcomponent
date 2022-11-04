/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin;

import net.atos.zac.app.admin.converter.RESTMailtemplateConverter;
import net.atos.zac.app.admin.model.RESTMailtemplate;
import net.atos.zac.mailtemplates.MailTemplateService;
import net.atos.zac.mailtemplates.model.MailTemplate;
import net.atos.zac.policy.PolicyService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.util.List;

import static net.atos.zac.policy.PolicyService.assertPolicy;

@Singleton
@Path("mailtemplates")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MailtemplateRESTService {

    @Inject
    private MailTemplateService mailTemplateService;

    @Inject
    private RESTMailtemplateConverter restMailtemplateConverter;

    @Inject
    private PolicyService policyService;

    @GET
    @Path("{id}")
    public RESTMailtemplate readMailtemplate(@PathParam("id") final long id) {
        assertPolicy(policyService.readOverigeRechten().getBeheren());
        return restMailtemplateConverter.convert(mailTemplateService.readMailtemplate(id));
    }

    @GET
    public List<RESTMailtemplate> listMailtemplates() {
        assertPolicy(policyService.readOverigeRechten().getBeheren());
        final List<MailTemplate> mailTemplates = mailTemplateService.listMailtemplates();
        return mailTemplates.stream()
                .map(mailtemplate -> restMailtemplateConverter.convert(mailtemplate))
                .toList();
    }

    @DELETE
    @Path("{id}")
    public void deleteMailtemplate(@PathParam("id") final long id) {
        assertPolicy(policyService.readOverigeRechten().getBeheren());
        mailTemplateService.delete(id);
    }

    @PUT
    @Path("{id}")
    public RESTMailtemplate persistMailtemplate(@PathParam("id") final long id,
            final RESTMailtemplate mailtemplate) {
        assertPolicy(policyService.readOverigeRechten().getBeheren());
        return restMailtemplateConverter.convert(
                mailTemplateService.persistMailtemplate(restMailtemplateConverter.convert(mailtemplate)));
    }
}
