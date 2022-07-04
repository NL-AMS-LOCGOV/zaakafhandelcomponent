/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.shared.model.Results;
import net.atos.client.zgw.zrc.model.RolMedewerker;
import net.atos.client.zgw.zrc.model.RolOrganisatorischeEenheid;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.zac.app.identity.converter.RESTGroupConverter;
import net.atos.zac.app.identity.converter.RESTUserConverter;
import net.atos.zac.app.zaken.model.RESTZaakOverzicht;
import net.atos.zac.datatable.Pagination;
import net.atos.zac.util.OpenZaakPaginationUtil;

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
        restZaakOverzicht.zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype()).getOmschrijving();
        restZaakOverzicht.openstaandeTaken = openstaandeTakenConverter.convert(zaak.getUuid());
        if (zaak.getStatus() != null) {
            restZaakOverzicht.status = restZaakStatusConverter.convertToStatusOmschrijving(zaak.getStatus());
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

        return restZaakOverzicht;
    }

    public List<RESTZaakOverzicht> convertZaakResults(final Results<Zaak> zaakResults, final Pagination pagination) {
        final List<RESTZaakOverzicht> zgwClientResults = OpenZaakPaginationUtil.filterPageFromOpenZaakResult(zaakResults.getResults().stream()
                                                                                                                     .map(this::convert)
                                                                                                                     .collect(Collectors.toList()), pagination);
        return zgwClientResults;
    }
}
