/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("fix")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FixRESTService {

    private static final Logger LOG = Logger.getLogger(FixRESTService.class.getName());

    @GET
    @Path("it")
    public Response fixIt() {
        LOG.info(">>> Nothing to fix!");
        return Response.noContent().build();
    }
}
