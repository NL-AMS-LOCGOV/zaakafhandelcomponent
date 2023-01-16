/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.policy;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import net.atos.client.opa.model.RuleQuery;
import net.atos.client.opa.model.RuleResponse;
import net.atos.zac.policy.input.DocumentInput;
import net.atos.zac.policy.input.TaakInput;
import net.atos.zac.policy.input.UserInput;
import net.atos.zac.policy.input.ZaakInput;
import net.atos.zac.policy.output.DocumentRechten;
import net.atos.zac.policy.output.OverigeRechten;
import net.atos.zac.policy.output.TaakRechten;
import net.atos.zac.policy.output.WerklijstRechten;
import net.atos.zac.policy.output.ZaakRechten;

@RegisterRestClient(configKey = "OPA-Api-Client")
@Path("v1/data/net/atos/zac")
@Produces(APPLICATION_JSON)
public interface OPAEvaluationClient {

    @POST
    @Path("zaak/zaak_rechten")
    RuleResponse<ZaakRechten> readZaakRechten(final RuleQuery<ZaakInput> query);

    @POST
    @Path("taak/taak_rechten")
    RuleResponse<TaakRechten> readTaakRechten(final RuleQuery<TaakInput> query);

    @POST
    @Path("document/document_rechten")
    RuleResponse<DocumentRechten> readDocumentRechten(final RuleQuery<DocumentInput> query);

    @POST
    @Path("overig/overige_rechten")
    RuleResponse<OverigeRechten> readOverigeRechten(final RuleQuery<UserInput> query);

    @POST
    @Path("werklijst/werklijst_rechten")
    RuleResponse<WerklijstRechten> readWerklijstRechten(final RuleQuery<UserInput> query);
}
