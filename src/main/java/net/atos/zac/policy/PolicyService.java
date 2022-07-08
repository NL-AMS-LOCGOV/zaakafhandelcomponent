/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.policy;

import static net.atos.client.zgw.drc.model.InformatieobjectStatus.DEFINITIEF;
import static net.atos.zac.configuratie.ConfiguratieService.STATUSTYPE_OMSCHRIJVING_HEROPEND;

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
import net.atos.client.zgw.drc.model.AbstractEnkelvoudigInformatieobject;
import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.RolMedewerker;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.policy.input.EnkelvoudigInformatieobjectData;
import net.atos.zac.policy.input.EnkelvoudigInformatieobjectInput;
import net.atos.zac.policy.input.UserInput;
import net.atos.zac.policy.input.ZaakData;
import net.atos.zac.policy.input.ZaakInput;
import net.atos.zac.policy.output.AppActies;
import net.atos.zac.policy.output.EnkelvoudigInformatieobjectActies;
import net.atos.zac.policy.output.ZaakActies;
import net.atos.zac.shared.exception.FoutmeldingException;

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

    public ZaakActies readZaakActies(final Zaak zaak) {
        final ZaakData zaakData = createZaakData(zaak);
        return evaluationClient.readZaakActies(new RuleQuery<>(new ZaakInput(loggedInUserInstance.get(), zaakData))).getResult();
    }

    public ZaakActies readZaakActies(final UUID zaakUUID) {
        return readZaakActies(zrcClientService.readZaak(zaakUUID));
    }

    public ZaakActies readZaakActies(final Zaak zaak, final boolean heropend, final String behandelaar) {
        final ZaakData zaakData = createZaakData(zaak, heropend, behandelaar);
        return evaluationClient.readZaakActies(new RuleQuery<>(new ZaakInput(loggedInUserInstance.get(), zaakData))).getResult();
    }

    public AppActies readAppActies() {
        return evaluationClient.readAppActies(new RuleQuery<>(new UserInput(loggedInUserInstance.get()))).getResult();
    }

    public EnkelvoudigInformatieobjectActies readEnkelvoudigInformatieobjectActies(final AbstractEnkelvoudigInformatieobject enkelvoudigInformatieobject,
            final String vergrendeldDoor, final Zaak zaak) {
        final EnkelvoudigInformatieobjectData enkelvoudigInformatieobjectData = createEnkelvoudigInformatieobjectData(enkelvoudigInformatieobject,
                                                                                                                      vergrendeldDoor);
        final ZaakData zaakData = zaak != null ? createZaakData(zaak) : null;
        return evaluationClient.readEnkelvoudigInformatieobjectActies(new RuleQuery<>(
                new EnkelvoudigInformatieobjectInput(loggedInUserInstance.get(), enkelvoudigInformatieobjectData, zaakData))).getResult();
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

    public static void assertActie(final boolean actie) {
        if (!actie) {
            throw new RuntimeException("Actie niet toegestaan");
        }
    }

    public void valideerZaakAfsluiten(final Zaak zaak) {
        assertActie(readZaakActies(zaak).getAfsluiten());
        if (zaak.getDeelzaken().stream().map(zrcClientService::readZaak).filter(Zaak::isOpen).findAny().isPresent()) {
            throw new FoutmeldingException("Zaak kan niet worden afgesloten want er zijn nog openstaande deelzaken.");
        }
    }

    private Set<String> readZaaktypeIdentificaties() {
        final RuleQuery<UserInput> query = new RuleQuery<>(new UserInput(loggedInUserInstance.get()));
        final RuleResponse<List<List<String>>> response = evaluationClient.readZaaktypen(query);
        return response.getResult().stream().flatMap(Collection::stream).collect(Collectors.toUnmodifiableSet());
    }

    private ZaakData createZaakData(final Zaak zaak, final boolean heropend, final String behandelaar) {
        final ZaakData zaakData = new ZaakData();
        zaakData.open = zaak.isOpen();
        zaakData.opgeschort = zaak.isOpgeschort();
        zaakData.heropend = heropend;
        zaakData.behandelaar = behandelaar;
        return zaakData;
    }

    private ZaakData createZaakData(final Zaak zaak) {
        final RolMedewerker behandelaar = zgwApiService.findBehandelaarForZaak(zaak);
        return createZaakData(zaak, zgwApiService.matchZaakStatustypeOmschrijving(zaak, STATUSTYPE_OMSCHRIJVING_HEROPEND),
                              behandelaar != null ? behandelaar.getBetrokkeneIdentificatie().getIdentificatie() : null);
    }

    private EnkelvoudigInformatieobjectData createEnkelvoudigInformatieobjectData(final AbstractEnkelvoudigInformatieobject enkelvoudigInformatieobject,
            final String vergrendeldDoor) {
        final EnkelvoudigInformatieobjectData enkelvoudigInformatieobjectData = new EnkelvoudigInformatieobjectData();
        enkelvoudigInformatieobjectData.definitief = enkelvoudigInformatieobject.getStatus() == DEFINITIEF;
        enkelvoudigInformatieobjectData.vergrendeld = enkelvoudigInformatieobject.getLocked();
        enkelvoudigInformatieobjectData.vergrendeldDoor = vergrendeldDoor;
        return enkelvoudigInformatieobjectData;
    }
}
