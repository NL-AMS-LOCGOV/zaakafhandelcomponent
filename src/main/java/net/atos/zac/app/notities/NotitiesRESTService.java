/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.notities;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.atos.zac.app.notities.converter.NotitieConverter;
import net.atos.zac.app.notities.model.RESTNotitie;
import net.atos.zac.notities.NotitieService;
import net.atos.zac.notities.model.Notitie;

@Singleton
@Path("notities")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NotitiesRESTService {

    @Inject
    private NotitieService notitieService;

    @Inject
    private NotitieConverter notitieConverter;

    @GET
    @Path("{type}/{uuid}")
    public List<RESTNotitie> listNotities(@PathParam("type") final String type, @PathParam("uuid") final String uuid) {
        final UUID notitieUUID = UUID.fromString(uuid);
        return notitieService.listNotitiesForZaak(notitieUUID).stream()
                .map(notitieConverter::convertToRESTNotitie)
                .collect(Collectors.toList());
    }

    @POST
    public RESTNotitie createNotitie(final RESTNotitie restNotitie) {
        final Notitie notitie = notitieConverter.convertToNotitie(restNotitie);
        return notitieConverter.convertToRESTNotitie(notitieService.createNotitie(notitie));
    }

    @PATCH
    public RESTNotitie updateNotitie(final RESTNotitie restNotitie) {
        final Notitie notitie = notitieConverter.convertToNotitie(restNotitie);
        return notitieConverter.convertToRESTNotitie(notitieService.updateNotitie(notitie));
    }

    @DELETE
    @Path("{id}")
    public void deleteNotitie(@PathParam("id") final Long id) {
        notitieService.deleteNotitie(id);
    }
}
