/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.policy;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import net.atos.client.opa.model.RuleQuery;
import net.atos.client.opa.model.RuleResponse;
import net.atos.zac.policy.input.UserInput;
import net.atos.zac.policy.input.ZaakInput;
import net.atos.zac.policy.output.AppActies;
import net.atos.zac.policy.output.ZaakActies;

@RegisterRestClient(configKey = "OPA-Api-Client")
@Path("v1/data/net/atos/zac")
@Produces(APPLICATION_JSON)
public interface OPAEvaluationClient {

    @POST
    @Path("zaak_acties")
    RuleResponse<ZaakActies> readZaakActies(final RuleQuery<ZaakInput> query);

    @POST
    @Path("app_acties")
    RuleResponse<AppActies> readAppActies(final RuleQuery<UserInput> query);

    @POST
    @Path("zaaktypen")
    RuleResponse<List<List<String>>> readZaaktypen(final RuleQuery<UserInput> query);
}
