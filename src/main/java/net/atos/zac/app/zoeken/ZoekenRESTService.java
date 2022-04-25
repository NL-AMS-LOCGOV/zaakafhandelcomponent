/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.atos.zac.app.zoeken.converter.RESTZoekZaakParametersConverter;
import net.atos.zac.app.zoeken.converter.RESTZoekZaakResultaatConverter;
import net.atos.zac.app.zoeken.model.RESTZoekZaakParameters;
import net.atos.zac.app.zoeken.model.RESTZoekZaakResultaat;
import net.atos.zac.zoeken.ZoekenService;
import net.atos.zac.zoeken.model.ZoekZaakResultaat;

@Path("zoeken")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class ZoekenRESTService {

    @Inject
    private ZoekenService zoekenService;

    @Inject
    private RESTZoekZaakParametersConverter zoekZaakParametersConverter;

    @Inject
    private RESTZoekZaakResultaatConverter zoekZaakResultaatConverter;

    @GET
    @Path("zaken")
    public List<RESTZoekZaakResultaat> zoekZaken(@BeanParam final RESTZoekZaakParameters zoekZaakParameters) {
        final List<ZoekZaakResultaat> zoekZaakResultaten = zoekenService.zoekZaak(zoekZaakParametersConverter.convert(zoekZaakParameters));
        return zoekZaakResultaatConverter.convert(zoekZaakResultaten);
    }
}
