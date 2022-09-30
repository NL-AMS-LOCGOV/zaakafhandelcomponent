/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import javax.inject.Inject;

import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.RolMedewerker;
import net.atos.client.zgw.zrc.model.RolOrganisatorischeEenheid;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Statustype;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.app.identity.converter.RESTGroupConverter;
import net.atos.zac.app.identity.converter.RESTUserConverter;
import net.atos.zac.app.policy.converter.RESTActiesConverter;
import net.atos.zac.app.zaken.model.RESTZaakOverzicht;
import net.atos.zac.policy.PolicyService;
import net.atos.zac.policy.output.ZaakActies;

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
    private RESTActiesConverter actiesConverter;

    @Inject
    private PolicyService policyService;

    @Inject
    private ZRCClientService zrcClientService;

    public RESTZaakOverzicht convert(final Zaak zaak) {
        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype());
        final Statustype statustype = zaak.getStatus() != null ?
                ztcClientService.readStatustype(zrcClientService.readStatus(zaak.getStatus()).getStatustype()) : null;
        final RolMedewerker behandelaar = zgwApiService.findBehandelaarForZaak(zaak);
        final RolOrganisatorischeEenheid groep = zgwApiService.findGroepForZaak(zaak);
        final ZaakActies zaakActies = policyService.readZaakActies(zaak, zaaktype, statustype, behandelaar);
        final RESTZaakOverzicht restZaakOverzicht = new RESTZaakOverzicht();
        restZaakOverzicht.uuid = zaak.getUuid();
        restZaakOverzicht.identificatie = zaak.getIdentificatie();
        restZaakOverzicht.acties = actiesConverter.convert(zaakActies);
        if (zaakActies.getLezen()) {
            restZaakOverzicht.startdatum = zaak.getStartdatum();
            restZaakOverzicht.einddatum = zaak.getEinddatum();
            restZaakOverzicht.einddatumGepland = zaak.getEinddatumGepland();
            restZaakOverzicht.uiterlijkeEinddatumAfdoening = zaak.getUiterlijkeEinddatumAfdoening();
            restZaakOverzicht.toelichting = zaak.getToelichting();
            restZaakOverzicht.omschrijving = zaak.getOmschrijving();
            restZaakOverzicht.zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype()).getOmschrijving();
            restZaakOverzicht.openstaandeTaken = openstaandeTakenConverter.convert(zaak.getUuid());
            restZaakOverzicht.resultaat = zaakResultaatConverter.convert(zaak.getResultaat());
            if (statustype != null) {
                restZaakOverzicht.status = statustype.getOmschrijving();
            }
            if (behandelaar != null) {
                restZaakOverzicht.behandelaar = userConverter.convertUserId(behandelaar.getBetrokkeneIdentificatie().getIdentificatie());
            }
            if (groep != null) {
                restZaakOverzicht.groep = groupConverter.convertGroupId(groep.getBetrokkeneIdentificatie().getIdentificatie());
            }
        }
        return restZaakOverzicht;
    }
}
