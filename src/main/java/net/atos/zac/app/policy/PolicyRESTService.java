/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.policy;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.atos.zac.app.policy.model.RESTAppActies;
import net.atos.zac.app.policy.model.RESTZakenActies;
import net.atos.zac.policy.PolicyService;

@Path("policy")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class PolicyRESTService {

    @Inject
    private PolicyService policyService;

    @GET
    @Path("appActies")
    public RESTAppActies readAppActies() {
        return new RESTAppActies(policyService.readAppActies());
    }

    @GET
    @Path("zakenActies")
    public RESTZakenActies readZakenActies() {
        return new RESTZakenActies(policyService.readZakenActies());
    }
}
