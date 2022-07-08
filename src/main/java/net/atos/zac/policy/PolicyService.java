/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.policy;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.opa.model.RuleQuery;
import net.atos.client.opa.model.RuleResponse;
import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.RolMedewerker;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.policy.input.UserInput;
import net.atos.zac.policy.input.ZaakData;
import net.atos.zac.policy.input.ZaakInput;
import net.atos.zac.policy.output.AppActies;
import net.atos.zac.policy.output.ZaakActies;

@ApplicationScoped
public class PolicyService {

    private static final String ALLE_ZAAKTYPEN = "-ALLE-ZAAKTYPEN-";

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    @Inject
    @RestClient
    private OPAEvaluationClient evaluationClient;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private ZGWApiService zgwApiService;

    public ZaakActies readZaakActies(final UUID zaakUUID) {
        return readZaakActies(zrcClientService.readZaak(zaakUUID));
    }

    public ZaakActies readZaakActies(final Zaak zaak) {
        return readZaakActies(zaak, zgwApiService.isZaakHeropend(zaak), getBehandelaar(zaak), heeftOpenDeelzaken(zaak));
    }

    public ZaakActies readZaakActies(final Zaak zaak, final boolean heropend, final String behandelaar, final boolean openDeelzaken) {
        final ZaakData zaakData = new ZaakData();
        zaakData.open = zaak.isOpen();
        zaakData.opgeschort = zaak.isOpgeschort();
        zaakData.heropend = heropend;
        zaakData.behandelaar = behandelaar;
        zaakData.openDeelzaken = openDeelzaken;
        return evaluationClient.readZaakActies(new RuleQuery<>(new ZaakInput(loggedInUserInstance.get(), zaakData))).getResult();
    }

    public AppActies readAppActies() {
        return evaluationClient.readAppActies(new RuleQuery<>(new UserInput(loggedInUserInstance.get()))).getResult();
    }

    public List<Zaaktype> filterAllowedZaaktypen(final List<Zaaktype> alleZaaktypen) {
        final Set<String> zaaktypeIdentificaties = readZaaktypeIdentificaties();
        if (zaaktypeIdentificaties.contains(ALLE_ZAAKTYPEN)) {
            return alleZaaktypen;
        } else {
            return alleZaaktypen.stream().filter(zaaktype -> zaaktypeIdentificaties.contains(zaaktype.getIdentificatie())).toList();
        }
    }

    public boolean isZaaktypeAllowed(final String zaaktypeIdentificatie) {
        final Set<String> zaaktypeIdentificaties = readZaaktypeIdentificaties();
        return zaaktypeIdentificaties.contains(ALLE_ZAAKTYPEN) || zaaktypeIdentificaties.contains(zaaktypeIdentificatie);
    }

    private Set<String> readZaaktypeIdentificaties() {
        final RuleQuery<UserInput> query = new RuleQuery<>(new UserInput(loggedInUserInstance.get()));
        final RuleResponse<List<List<String>>> response = evaluationClient.readZaaktypen(query);
        return response.getResult().stream().flatMap(Collection::stream).collect(Collectors.toUnmodifiableSet());
    }

    private String getBehandelaar(final Zaak zaak) {
        final RolMedewerker behandelaar = zgwApiService.findBehandelaarForZaak(zaak);
        return behandelaar != null ? behandelaar.getBetrokkeneIdentificatie().getIdentificatie() : null;
    }

    private boolean heeftOpenDeelzaken(final Zaak zaak) {
        return zaak.getDeelzaken().stream().map(zrcClientService::readZaak).filter(Zaak::isOpen).findAny().isPresent();
    }
}
