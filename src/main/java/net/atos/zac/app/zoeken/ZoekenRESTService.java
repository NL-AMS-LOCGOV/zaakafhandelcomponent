/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.atos.zac.app.zoeken.converter.RESTZoekParametersConverter;
import net.atos.zac.app.zoeken.converter.RESTZoekResultaatConverter;
import net.atos.zac.app.zoeken.model.RESTZaakZoekObject;
import net.atos.zac.app.zoeken.model.RESTZoekParameters;
import net.atos.zac.app.zoeken.model.RESTZoekResultaat;
import net.atos.zac.zoeken.ZoekenService;
import net.atos.zac.zoeken.model.ZaakZoekObject;
import net.atos.zac.zoeken.model.ZoekParameters;
import net.atos.zac.zoeken.model.ZoekResultaat;

@Path("zoeken")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class ZoekenRESTService {

    private static final Logger LOG = Logger.getLogger(ZoekenRESTService.class.getName());

    @Inject
    private ZoekenService zoekenService;

    @Inject
    private RESTZoekParametersConverter zoekZaakParametersConverter;

    @Inject
    private RESTZoekResultaatConverter ZoekResultaatConverter;

    @PUT
    @Path("list")
    public RESTZoekResultaat<RESTZaakZoekObject> listZoekResultaat(final RESTZoekParameters restZaakParameters) {
        try {
            final ZoekParameters zoekParameters = zoekZaakParametersConverter.convert(restZaakParameters);
            final ZoekResultaat<ZaakZoekObject> zoekResultaat = zoekenService.zoek(zoekParameters);
            return ZoekResultaatConverter.convert(zoekResultaat, restZaakParameters);
        } catch (final RuntimeException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
            return new RESTZoekResultaat<>(e.getMessage());
        }
    }

}
