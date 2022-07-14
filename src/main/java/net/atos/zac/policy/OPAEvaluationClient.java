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
import net.atos.zac.policy.input.EnkelvoudigInformatieobjectInput;
import net.atos.zac.policy.input.TaakInput;
import net.atos.zac.policy.input.UserInput;
import net.atos.zac.policy.input.ZaakInput;
import net.atos.zac.policy.output.AppActies;
import net.atos.zac.policy.output.EnkelvoudigInformatieobjectActies;
import net.atos.zac.policy.output.TaakActies;
import net.atos.zac.policy.output.TakenActies;
import net.atos.zac.policy.output.ZaakActies;
import net.atos.zac.policy.output.ZakenActies;

@RegisterRestClient(configKey = "OPA-Api-Client")
@Path("v1/data/net/atos/zac")
@Produces(APPLICATION_JSON)
public interface OPAEvaluationClient {

    @POST
    @Path("zaak/zaak_acties")
    RuleResponse<ZaakActies> readZaakActies(final RuleQuery<ZaakInput> query);

    @POST
    @Path("app/app_acties")
    RuleResponse<AppActies> readAppActies(final RuleQuery<UserInput> query);

    @POST
    @Path("zaaktype/zaaktypen")
    RuleResponse<List<List<String>>> readZaaktypen(final RuleQuery<UserInput> query);

    @POST
    @Path("enkelvoudiginformatieobject/enkelvoudig_informatieobject_acties")
    RuleResponse<EnkelvoudigInformatieobjectActies> readEnkelvoudigInformatieobjectActies(final RuleQuery<EnkelvoudigInformatieobjectInput> query);

    @POST
    @Path("zaken/zaken_acties")
    RuleResponse<ZakenActies> readZakenActies(final RuleQuery<UserInput> query);

    @POST
    @Path("taken/taken_acties")
    RuleResponse<TakenActies> readTakenActies(final RuleQuery<UserInput> query);

    @POST
    @Path("taak/taak_acties")
    RuleResponse<TaakActies> readTaakActies(final RuleQuery<TaakInput> query);

}
