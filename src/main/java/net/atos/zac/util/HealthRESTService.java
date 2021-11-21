/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import static net.atos.zac.util.ConfigurationService.CATALOGUS_DOMEIN;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.annotation.JsonbTransient;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.zgw.ztc.ZTCClient;
import net.atos.client.zgw.ztc.model.CatalogusListParameters;

@Path("health")
public class HealthRESTService {

    private static final Logger LOG = Logger.getLogger(HealthRESTService.class.getName());

    private static final String ALIVE_RESPONSE = "Ok!";

    private static final CatalogusListParameters CATALOGUS_LIST_PARAMETERS = new CatalogusListParameters();

    static {
        CATALOGUS_LIST_PARAMETERS.setDomein(CATALOGUS_DOMEIN);
    }

    @RestClient
    @Inject
    private ZTCClient ztcClient;

    @GET
    @Path("alive")
    @Produces(MediaType.TEXT_PLAIN)
    public Response alive() {
        LOG.finest(() -> String.format("Alive: %s", ALIVE_RESPONSE));
        return Response.ok().entity(ALIVE_RESPONSE).build();
    }

    @GET
    @Path("ready")
    @Produces(MediaType.APPLICATION_JSON)
    public Response ready() {
        final ReadyStatus readyStatus = new ReadyStatus();
        readyStatus.isDatabaseOk = checkDatabase();
        readyStatus.isOpenZaakOk = checkOpenZaak();
        final Response.ResponseBuilder responseBuilder;
        if (readyStatus.isEverythingOk()) {
            responseBuilder = Response.ok();
            LOG.finest(() -> String.format("ReadyStatus: %s", JsonbBuilder.create().toJson(readyStatus)));
        } else {
            responseBuilder = Response.serverError();
            LOG.severe(String.format("ReadyStatus: %s", JsonbBuilder.create().toJson(readyStatus)));
        }
        return responseBuilder.entity(readyStatus).build();
    }

    private boolean checkDatabase() {
        // ToDo: ESUITEDEV-25956
        return true;
    }

    private boolean checkOpenZaak() {
        try {
            return ztcClient.catalogusList(CATALOGUS_LIST_PARAMETERS).getCount() > 0;
        } catch (final RuntimeException e) {
            return false;
        }
    }

    public static class ReadyStatus {

        public Boolean isDatabaseOk;

        public Boolean isOpenZaakOk;

        @JsonbTransient
        public boolean isEverythingOk() {
            return isDatabaseOk && isOpenZaakOk;
        }
    }
}
