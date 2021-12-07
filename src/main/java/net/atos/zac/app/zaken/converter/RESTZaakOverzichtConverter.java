/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.shared.model.Results;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.zac.app.identity.converter.RESTGroepConverter;
import net.atos.zac.app.identity.converter.RESTMedewerkerConverter;
import net.atos.zac.app.zaken.model.RESTZaakOverzicht;
import net.atos.zac.app.zaken.model.RESTZaakStatus;
import net.atos.zac.authentication.IngelogdeMedewerker;
import net.atos.zac.authentication.Medewerker;
import net.atos.zac.datatable.Pagination;
import net.atos.zac.rechten.RechtOperatie;
import net.atos.zac.rechten.ZaakRechten;
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
    private RESTGroepConverter groepConverter;

    @Inject
    private RESTMedewerkerConverter medewerkerConverter;

    @Inject
    @IngelogdeMedewerker
    private Medewerker ingelogdeMedewerker;

    public RESTZaakOverzicht convert(final Zaak zaak) {
        final RESTZaakOverzicht restZaakOverzicht = new RESTZaakOverzicht();
        restZaakOverzicht.identificatie = zaak.getIdentificatie();
        restZaakOverzicht.uuid = zaak.getUuid().toString();
        restZaakOverzicht.startdatum = zaak.getStartdatum();
        restZaakOverzicht.einddatum = zaak.getEinddatum();
        restZaakOverzicht.einddatumGepland = zaak.getEinddatumGepland();
        restZaakOverzicht.uiterlijkeEinddatumAfdoening = zaak.getUiterlijkeEinddatumAfdoening();
        restZaakOverzicht.toelichting = zaak.getToelichting();
        restZaakOverzicht.zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype()).getOmschrijving();
        final RESTZaakStatus status = restZaakStatusConverter.convert(zaak.getStatus());
        if (status != null) {
            restZaakOverzicht.status = status.naam;
        }
//      restZaakOverzichtView.aanvrager

        final String groepId = zgwApiService.findGroepForZaak(zaak.getUrl())
                .filter(Objects::nonNull)
                .map(groep -> groep.getBetrokkeneIdentificatie().getIdentificatie())
                .orElse(null);
        restZaakOverzicht.groep = groepConverter.convertGroupId(groepId);

        final String behandelaarId = zgwApiService.findBehandelaarForZaak(zaak.getUrl())
                .filter(Objects::nonNull)
                .map(behandelaar -> behandelaar.getBetrokkeneIdentificatie().getIdentificatie())
                .orElse(null);
        restZaakOverzicht.behandelaar = medewerkerConverter.convertUserId(behandelaarId);

        restZaakOverzicht.rechten = getRechten(behandelaarId, groepId);

        restZaakOverzicht.resultaat = zaakResultaatConverter.convert(zaak.getResultaat());

        return restZaakOverzicht;
    }

    public List<RESTZaakOverzicht> convertZaakResults(final Results<Zaak> zaakResults, final Pagination pagination) {
        final List<RESTZaakOverzicht> zgwClientResults = OpenZaakPaginationUtil.filterPageFromOpenZaakResult(zaakResults.getResults().stream()
                                                                                                                     .map(this::convert)
                                                                                                                     .collect(Collectors.toList()), pagination);
        return zgwClientResults;
    }

    private Map<RechtOperatie, Boolean> getRechten(final String behandelaarId, final String groepId) {
        final Map<RechtOperatie, Boolean> rechten = new HashMap<>();

        rechten.put(RechtOperatie.TOEKENNEN_AAN_MIJ, ZaakRechten.isKenToeAanMijToegestaan(ingelogdeMedewerker, behandelaarId, groepId));

        return rechten;
    }
}
