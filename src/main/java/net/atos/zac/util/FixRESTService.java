/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.atos.zac.flowable.FlowableService;

@Path("fix")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FixRESTService {

    @Inject
    private FlowableService flowableService;

    @GET
    @Path("it")
    public Response fixIt() {
        flowableService.fixIt();
        return Response.noContent().build();
    }
}
