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
import net.atos.zac.app.zaken.model.RESTZaakOverzicht;
import net.atos.zac.policy.PolicyService;

public class RESTZaakOverzichtConverter {

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZGWApiService zgwApiService;

    @Inject
    private RESTZaakResultaatConverter zaakResultaatConverter;

    @Inject
    private RESTZaakStatusConverter restZaakStatusConverter;

    @Inject
    private RESTGroupConverter groupConverter;

    @Inject
    private RESTUserConverter userConverter;

    @Inject
    private RESTOpenstaandeTakenConverter openstaandeTakenConverter;

    @Inject
    private RESTZaakActiesConverter actiesConverter;

    @Inject
    private PolicyService policyService;

    @Inject
    private ZRCClientService zrcClientService;

    public RESTZaakOverzicht convert(final Zaak zaak) {
        final RESTZaakOverzicht restZaakOverzicht = new RESTZaakOverzicht();
        restZaakOverzicht.identificatie = zaak.getIdentificatie();
        restZaakOverzicht.uuid = zaak.getUuid().toString();
        restZaakOverzicht.startdatum = zaak.getStartdatum();
        restZaakOverzicht.einddatum = zaak.getEinddatum();
        restZaakOverzicht.einddatumGepland = zaak.getEinddatumGepland();
        restZaakOverzicht.uiterlijkeEinddatumAfdoening = zaak.getUiterlijkeEinddatumAfdoening();
        restZaakOverzicht.toelichting = zaak.getToelichting();
        restZaakOverzicht.omschrijving = zaak.getOmschrijving();
        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype());
        restZaakOverzicht.zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype()).getOmschrijving();
        restZaakOverzicht.openstaandeTaken = openstaandeTakenConverter.convert(zaak.getUuid());
        Statustype statustype = null;
        if (zaak.getStatus() != null) {
            statustype = ztcClientService.readStatustype(zrcClientService.readStatus(zaak.getStatus()).getStatustype());
            restZaakOverzicht.status = statustype.getOmschrijving();
        }

        final RolOrganisatorischeEenheid groep = zgwApiService.findGroepForZaak(zaak);
        if (groep != null) {
            restZaakOverzicht.groep = groupConverter.convertGroupId(groep.getBetrokkeneIdentificatie().getIdentificatie());
        }


        final RolMedewerker behandelaar = zgwApiService.findBehandelaarForZaak(zaak);
        if (behandelaar != null) {
            restZaakOverzicht.behandelaar = userConverter.convertUserId(behandelaar.getBetrokkeneIdentificatie().getIdentificatie());
        }

        restZaakOverzicht.resultaat = zaakResultaatConverter.convert(zaak.getResultaat());
        restZaakOverzicht.acties = actiesConverter.convert(policyService.readZaakActies(zaak, zaaktype, statustype, behandelaar));
        return restZaakOverzicht;
    }
}
