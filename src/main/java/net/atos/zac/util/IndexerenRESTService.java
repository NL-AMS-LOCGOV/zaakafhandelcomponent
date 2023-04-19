/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import java.util.Arrays;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.atos.zac.authentication.ActiveSession;
import net.atos.zac.authentication.SecurityUtil;
import net.atos.zac.zoeken.IndexeerService;
import net.atos.zac.zoeken.model.index.HerindexerenInfo;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

@Path("indexeren")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class IndexerenRESTService {

    @Inject
    private IndexeerService indexeerService;

    @Inject
    @ActiveSession
    private Instance<HttpSession> httpSession;

    @GET
    @Path("herindexeren/{type}")
    public HerindexerenInfo herindexeren(@PathParam("type") ZoekObjectType type) {
        return indexeerService.herindexeren(type);
    }

    /**
     * Indexeert eerst {aantal} taken, daarna {aantal} zaken.
     *
     * @param aantal 100 is een goede default waarde
     * @return het aantal resterende items na het uitvoeren van deze aanroep
     */
    @GET
    @Path("{aantal}")
    @Produces(MediaType.TEXT_PLAIN)
    public String indexeren(@PathParam("aantal") int aantal) {
        SecurityUtil.setFunctioneelGebruiker(httpSession.get());
        final StringBuilder info = new StringBuilder();
        Arrays.stream(ZoekObjectType.values()).forEach(type -> {
            final IndexeerService.Resultaat resultaat = indexeerService.indexeer(aantal, type);
            info.append(("[%s] geindexeerd: %d, verwijderd: %d, resterend: %d\n")
                                .formatted(type.toString(), resultaat.indexed(), resultaat.removed(),
                                           resultaat.remaining()));
        });
        return info.toString();
    }
}
