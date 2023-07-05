/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.formulieren;

import static net.atos.zac.policy.PolicyService.assertPolicy;

import java.util.List;

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

import net.atos.zac.app.formulieren.converter.RESTFormulierDefinitieConverter;
import net.atos.zac.app.formulieren.model.RESTFormulierDefinitie;
import net.atos.zac.formulieren.FormulierDefinitieService;
import net.atos.zac.policy.PolicyService;

@Singleton
@Path("formulierDefinities")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FormulierDefinitieRESTService {

    @Inject
    private FormulierDefinitieService service;

    @Inject
    private RESTFormulierDefinitieConverter converter;

    @Inject
    private PolicyService policyService;


    @GET
    public List<RESTFormulierDefinitie> list() {
        assertPolicy(policyService.readOverigeRechten().getBeheren());
        return service.listFormulierDefinities().stream()
                .map(formulierDefinitie -> converter.convert(formulierDefinitie, false))
                .toList();
    }

    @POST
    public RESTFormulierDefinitie create(final RESTFormulierDefinitie restFormulierDefinitie) {
        assertPolicy(policyService.readOverigeRechten().getBeheren());
        return converter.convert(
                service.createFormulierDefinitie(
                        converter.convert(restFormulierDefinitie)), true);
    }

    @GET
    @Path("{id}")
    public RESTFormulierDefinitie read(@PathParam("id") final long id) {
        assertPolicy(policyService.readOverigeRechten().getBeheren());
        return converter.convert(
                service.readFormulierDefinitie(id), true);
    }

    @PUT
    public RESTFormulierDefinitie update(final RESTFormulierDefinitie restFormulierDefinitie) {
        assertPolicy(policyService.readOverigeRechten().getBeheren());
        return converter.convert(
                service.updateFormulierDefinitie(
                        converter.convert(restFormulierDefinitie)), true);
    }

    @DELETE
    @Path("{id}")
    public void delete(@PathParam("id") final long id) {
        assertPolicy(policyService.readOverigeRechten().getBeheren());
        service.deleteFormulierDefinitie(id);
    }


}
