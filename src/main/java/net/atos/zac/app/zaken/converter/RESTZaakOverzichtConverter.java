/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import javax.inject.Inject;

import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.app.identity.converter.RESTGroupConverter;
import net.atos.zac.app.identity.converter.RESTUserConverter;
import net.atos.zac.app.policy.converter.RESTRechtenConverter;
import net.atos.zac.app.zaken.model.RESTZaakOverzicht;
import net.atos.zac.policy.PolicyService;
import net.atos.zac.policy.output.ZaakRechten;

public class RESTZaakOverzichtConverter {

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZGWApiService zgwApiService;

    @Inject
    private RESTZaakResultaatConverter zaakResultaatConverter;

    @Inject
    private RESTGroupConverter groupConverter;

    @Inject
    private RESTUserConverter userConverter;

    @Inject
    private RESTOpenstaandeTakenConverter openstaandeTakenConverter;

    @Inject
    private RESTRechtenConverter rechtenConverter;

    @Inject
    private PolicyService policyService;

    @Inject
    private ZRCClientService zrcClientService;

    public RESTZaakOverzicht convert(final Zaak zaak) {
        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype());
        final ZaakRechten zaakrechten = policyService.readZaakRechten(zaak);
        final RESTZaakOverzicht restZaakOverzicht = new RESTZaakOverzicht();
        restZaakOverzicht.uuid = zaak.getUuid();
        restZaakOverzicht.identificatie = zaak.getIdentificatie();
        restZaakOverzicht.rechten = rechtenConverter.convert(zaakrechten);
        if (zaakrechten.getLezen()) {
            restZaakOverzicht.startdatum = zaak.getStartdatum();
            restZaakOverzicht.einddatum = zaak.getEinddatum();
            restZaakOverzicht.einddatumGepland = zaak.getEinddatumGepland();
            restZaakOverzicht.uiterlijkeEinddatumAfdoening = zaak.getUiterlijkeEinddatumAfdoening();
            restZaakOverzicht.toelichting = zaak.getToelichting();
            restZaakOverzicht.omschrijving = zaak.getOmschrijving();
            restZaakOverzicht.zaaktype = zaaktype.getOmschrijving();
            restZaakOverzicht.openstaandeTaken = openstaandeTakenConverter.convert(zaak.getUuid());
            restZaakOverzicht.resultaat = zaakResultaatConverter.convert(zaak.getResultaat());
            if (zaak.getStatus() != null) {
                restZaakOverzicht.status = ztcClientService.readStatustype(
                        zrcClientService.readStatus(zaak.getStatus()).getStatustype()).getOmschrijving();
            }
            zgwApiService.findBehandelaarForZaak(zaak)
                    .map(behandelaar -> userConverter.convertUserId(
                            behandelaar.getBetrokkeneIdentificatie().getIdentificatie()))
                    .ifPresent(behandelaar -> restZaakOverzicht.behandelaar = behandelaar);
            zgwApiService.findGroepForZaak(zaak)
                    .map(groep -> groupConverter.convertGroupId(groep.getBetrokkeneIdentificatie().getIdentificatie()))
                    .ifPresent(groep -> restZaakOverzicht.groep = groep);
        }
        return restZaakOverzicht;
    }
}
