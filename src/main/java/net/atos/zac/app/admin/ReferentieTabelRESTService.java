/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin;

import static net.atos.zac.policy.PolicyService.assertPolicy;
import static net.atos.zac.zaaksturing.model.ReferentieTabel.Systeem.AFZENDER;
import static net.atos.zac.zaaksturing.model.ReferentieTabel.Systeem.DOMEIN;

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

import net.atos.zac.app.admin.converter.RESTReferentieTabelConverter;
import net.atos.zac.app.admin.converter.RESTReferentieWaardeConverter;
import net.atos.zac.app.admin.model.RESTReferentieTabel;
import net.atos.zac.policy.PolicyService;
import net.atos.zac.zaaksturing.ReferentieTabelBeheerService;
import net.atos.zac.zaaksturing.ReferentieTabelService;
import net.atos.zac.zaaksturing.model.ReferentieTabel;

@Singleton
@Path("referentietabellen")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ReferentieTabelRESTService {
    @Inject
    private ReferentieTabelService referentieTabelService;

    @Inject
    private ReferentieTabelBeheerService referentieTabelBeheerService;

    @Inject
    private RESTReferentieTabelConverter restReferentieTabelConverter;

    @Inject
    private RESTReferentieWaardeConverter restReferentieWaardeConverter;

    @Inject
    private PolicyService policyService;

    @GET
    public List<RESTReferentieTabel> listReferentieTabellen() {
        assertPolicy(policyService.readOverigeRechten().getBeheren());
        final List<ReferentieTabel> referentieTabellen = referentieTabelService.listReferentieTabellen();
        return referentieTabellen.stream()
                .map(referentieTabel -> restReferentieTabelConverter.convert(referentieTabel, false))
                .toList();
    }

    @GET
    @Path("new")
    public RESTReferentieTabel newReferentieTabel() {
        assertPolicy(policyService.readOverigeRechten().getBeheren());
        return restReferentieTabelConverter.convert(
                referentieTabelBeheerService.newReferentieTabel(), true);
    }

    @POST
    public RESTReferentieTabel createReferentieTabel(final RESTReferentieTabel referentieTabel) {
        assertPolicy(policyService.readOverigeRechten().getBeheren());
        return restReferentieTabelConverter.convert(
                referentieTabelBeheerService.createReferentieTabel(
                        restReferentieTabelConverter.convert(referentieTabel)), true);
    }

    @GET
    @Path("{id}")
    public RESTReferentieTabel readReferentieTabel(@PathParam("id") final long id) {
        assertPolicy(policyService.readOverigeRechten().getBeheren());
        return restReferentieTabelConverter.convert(
                referentieTabelService.readReferentieTabel(id), true);
    }

    @PUT
    @Path("{id}")
    public RESTReferentieTabel updateReferentieTabel(@PathParam("id") final long id,
            final RESTReferentieTabel referentieTabel) {
        assertPolicy(policyService.readOverigeRechten().getBeheren());
        return restReferentieTabelConverter.convert(
                referentieTabelBeheerService.updateReferentieTabel(
                        restReferentieTabelConverter.convert(referentieTabel,
                                                             referentieTabelService.readReferentieTabel(id))), true);
    }

    @DELETE
    @Path("{id}")
    public void deleteReferentieTabel(@PathParam("id") final long id) {
        assertPolicy(policyService.readOverigeRechten().getBeheren());
        referentieTabelBeheerService.deleteReferentieTabel(id);
    }

    @GET
    @Path("afzender")
    public List<String> listAfzenders() {
        assertPolicy(policyService.readOverigeRechten().getBeheren());
        return restReferentieWaardeConverter.convert(
                referentieTabelService.readReferentieTabel(AFZENDER.name()).getWaarden());
    }

    @GET
    @Path("domein")
    public List<String> listDomeinen() {
        assertPolicy(policyService.readOverigeRechten().getBeheren());
        return restReferentieWaardeConverter.convert(
                referentieTabelService.readReferentieTabel(DOMEIN.name()).getWaarden());
    }
}
