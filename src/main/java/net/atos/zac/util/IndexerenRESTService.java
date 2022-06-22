/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.atos.zac.zoeken.IndexeerService;
import net.atos.zac.zoeken.model.index.HerindexeerInfo;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

@Path("indexeren")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class IndexerenRESTService {

    @Inject
    private IndexeerService indexeerService;

    @GET
    @Path("herindexeren/{type}")
    public HerindexeerInfo herindexeer(@PathParam("type") ZoekObjectType type) {
        return indexeerService.herindexeren(type);
    }

    /**
     * @param type   ZAAK en TAAK is momenteel geïmplementeerd
     * @param aantal 100 is een mooi aantal
     * @return het aantal resterende items na het uitvoeren van deze aanroep
     */
    @GET
    @Path("{type}/{aantal}")
    public String indexeer(@PathParam("type") ZoekObjectType type, @PathParam("aantal") int aantal) {
        return switch (type) {
            case ZAAK, TAAK -> {
                int aantalResterend = indexeerService.indexeer(aantal, type);
                yield "\"Aantal items resterend: %d\"\n".formatted(aantalResterend);
            }
        };
    }
}
