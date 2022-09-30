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

import net.atos.zac.app.policy.converter.RESTActiesConverter;
import net.atos.zac.app.policy.model.RESTOverigActies;
import net.atos.zac.app.policy.model.RESTWerklijstActies;
import net.atos.zac.policy.PolicyService;

@Path("policy")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class PolicyRESTService {

    @Inject
    private PolicyService policyService;

    @Inject
    private RESTActiesConverter actiesConverter;

    @GET
    @Path("werklijstActies")
    public RESTWerklijstActies readWerklijstActies() {
        return actiesConverter.convert(policyService.readWerklijstActies());
    }

    @GET
    @Path("overigActies")
    public RESTOverigActies readOverigActies() {
        return actiesConverter.convert(policyService.readOverigActies());
    }
}
