/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.atos.zac.app.shared.RESTResult;
import net.atos.zac.app.zoeken.converter.RESTSolrZoekParametersConverter;
import net.atos.zac.app.zoeken.converter.RESTSolrZoekResultaatConverter;
import net.atos.zac.app.zoeken.model.RESTSolrZoekParameters;
import net.atos.zac.app.zoeken.model.RESTZaakZoekObject;
import net.atos.zac.zoeken.ZoekenService;
import net.atos.zac.zoeken.model.ZaakZoekObject;
import net.atos.zac.zoeken.model.ZoekResultaat;

@Path("zoeken")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class ZoekenRESTService {

    @Inject
    private ZoekenService zoekenService;

    @Inject
    private RESTSolrZoekParametersConverter zoekZaakParametersConverter;

    @Inject
    private RESTSolrZoekResultaatConverter solrZoekResultaatConverter;

    @GET
    @Path("list")
    public RESTResult<RESTZaakZoekObject> listZoekResultaat(@BeanParam final RESTSolrZoekParameters zoekZaakParameters) {
        final ZoekResultaat<ZaakZoekObject> zoekResultaat = zoekenService.zoekZaak(zoekZaakParametersConverter.convert(zoekZaakParameters));
        return solrZoekResultaatConverter.convert(zoekResultaat);
    }
}
