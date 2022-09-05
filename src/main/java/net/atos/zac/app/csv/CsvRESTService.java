/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.csv;

import net.atos.zac.app.zoeken.converter.RESTZoekParametersConverter;
import net.atos.zac.app.zoeken.model.RESTZoekParameters;
import net.atos.zac.csv.CsvService;
import net.atos.zac.zoeken.ZoekenService;
import net.atos.zac.zoeken.model.ZoekObject;
import net.atos.zac.zoeken.model.ZoekParameters;
import net.atos.zac.zoeken.model.ZoekResultaat;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

@Path("csv")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class CsvRESTService {

    @Inject
    private ZoekenService zoekenService;

    @Inject
    private RESTZoekParametersConverter restZoekParametersConverter;

    @Inject
    private CsvService csvService;

    @POST
    @Path("export")
    public Response downloadCSV(final RESTZoekParameters restZoekParameters) {
        final ZoekParameters zoekParameters = restZoekParametersConverter.convert(restZoekParameters);
        zoekParameters.setRows(Integer.MAX_VALUE);
        final ZoekResultaat<? extends ZoekObject> zoekResultaat = zoekenService.zoek(zoekParameters);

        final StreamingOutput streamingOutput = csvService.exportToCsv(zoekResultaat);

        return Response.ok(streamingOutput).header("Content-Type", "text/csv").build();
    }
}
