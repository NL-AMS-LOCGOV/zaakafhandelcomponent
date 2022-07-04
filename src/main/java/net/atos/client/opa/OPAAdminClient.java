/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.opa;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "OPA-Api-Client")
@Path("v1/policies")
@Consumes(TEXT_PLAIN)
@Produces(APPLICATION_JSON)
public interface OPAAdminClient {

    @PUT
    @Path("{id}")
    Response policyUpdate(@PathParam("id") final String id, final String policy);
}
