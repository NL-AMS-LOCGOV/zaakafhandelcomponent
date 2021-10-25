/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.inject.Inject;

import net.atos.client.zgw.shared.model.Results;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.zac.app.identity.converter.RESTGroepConverter;
import net.atos.zac.app.identity.converter.RESTMedewerkerConverter;
import net.atos.zac.app.identity.model.RESTGroep;
import net.atos.zac.app.util.datatable.Pagination;
import net.atos.zac.app.zaken.model.RESTZaakOverzicht;
import net.atos.zac.app.zaken.model.RESTZaakStatus;
import net.atos.zac.handle.HandleService;
import net.atos.zac.util.PaginationUtil;

public class RESTZaakOverzichtConverter {

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private RESTZaakStatusConverter restZaakStatusConverter;

    @Inject
    private RESTMedewerkerConverter restMedewerkerConverter;

    @Inject
    private RESTGroepConverter groepConverter;

    @EJB
    private HandleService handleService;

    public RESTZaakOverzicht convert(final Zaak zaak) {
        final RESTZaakOverzicht restZaakOverzicht = new RESTZaakOverzicht();
        restZaakOverzicht.identificatie = zaak.getIdentificatie();
        restZaakOverzicht.uuid = zaak.getUuid().toString();
        restZaakOverzicht.startdatum = zaak.getStartdatum();
        restZaakOverzicht.toelichting = zaak.getToelichting();
        restZaakOverzicht.zaaktype = ztcClientService.getZaaktype(zaak.getZaaktype()).getOmschrijving();
        final RESTZaakStatus status = restZaakStatusConverter.convert(zaak.getStatus());
        if (status != null) {
            restZaakOverzicht.status = status.naam;
        }
        restZaakOverzicht.uiterlijkedatumafdoening = zaak.getUiterlijkeEinddatumAfdoening();
//      restZaakOverzichtView.aanvrager

        // TODO ESUITEDEV-25802 Behandelaar en groep per zaak voor de werkvoorraad ophalen
        // restZaakOverzicht.behandelaar = ~
        // restZaakOverzicht.groep = getGroep(zaak.getZaaktype(), zaak.getUrl());

        return restZaakOverzicht;
    }

    public List<RESTZaakOverzicht> convertZaakResults(final Results<Zaak> zaakResults, final Pagination pagination) {
        return PaginationUtil.getZGWClientResults(zaakResults.getResults().stream()
                                                          .map(this::convert)
                                                          .collect(Collectors.toList()), pagination);
    }

    // TODO opruimen als ESUITEDEV-25802 een betere oplossing biedt
    private RESTGroep getGroep(final URI zaaktypeURI, final URI zaakURI) {
        final String groupId = handleService.ophalenZaakBehandelaarGroepId(zaakURI, zaaktypeURI);
        return groupId != null ? groepConverter.convertGroupId(groupId) : null;
    }
}
