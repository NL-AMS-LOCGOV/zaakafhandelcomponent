/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaakdata;

import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.atos.zac.app.zaakdata.converter.RESTZaakdataConverter;
import net.atos.zac.app.zaakdata.model.RESTZaakdata;
import net.atos.zac.zaakdata.Zaakdata;
import net.atos.zac.zaakdata.ZaakdataService;

/**
 *
 */
@Path("zaakdata")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ZaakdataRESTService {

    @Inject
    private ZaakdataService zaakdataService;

    @Inject
    private RESTZaakdataConverter zaakdataConverter;

    @GET
    @Path("zaak/{zaak-uuid}")
    public RESTZaakdata getZaakdata(@PathParam("zaak-uuid") final UUID zaakUUID) {
        final Zaakdata zaakdata = zaakdataService.readZaakdata(zaakUUID);
        return zaakdataConverter.convert(zaakdata);
    }

    @PUT
    @Path("zaak/{zaak-uuid}")
    public RESTZaakdata putZaakdata(@PathParam("zaak-uuid") final UUID zaakUUID, final RESTZaakdata restZaakdata) {
        final Zaakdata zaakdata = zaakdataConverter.convert(restZaakdata);
        final Zaakdata updatedZaakdata = zaakdataService.updateZaakdata(zaakUUID, zaakdata);
        return zaakdataConverter.convert(updatedZaakdata);
    }
}
