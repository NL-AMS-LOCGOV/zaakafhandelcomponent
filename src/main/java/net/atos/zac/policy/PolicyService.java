/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.policy;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.opa.model.RuleQuery;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.policy.input.ZaakData;
import net.atos.zac.policy.input.ZaakInput;
import net.atos.zac.policy.output.ZaakActies;

@ApplicationScoped
public class PolicyService {

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    @Inject
    @RestClient
    private OPAEvaluationClient evaluationClient;

    public ZaakActies readZaakActies(final Zaak zaak, final boolean heropend, final String behandelaar, final boolean openDeelzaken) {
        final ZaakData zaakData = new ZaakData();
        zaakData.open = zaak.isOpen();
        zaakData.opgeschort = zaak.isOpgeschort();
        zaakData.heropend = heropend;
        zaakData.behandelaar = behandelaar;
        zaakData.openDeelzaken = openDeelzaken;
        return evaluationClient.readZaakActies(new RuleQuery<>(new ZaakInput(loggedInUserInstance.get(), zaakData))).getResult();
    }
}
