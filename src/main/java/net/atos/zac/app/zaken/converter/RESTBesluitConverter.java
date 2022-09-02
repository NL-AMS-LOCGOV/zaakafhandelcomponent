/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import java.time.LocalDate;

import javax.inject.Inject;

import net.atos.client.zgw.brc.model.Besluit;
import net.atos.zac.app.zaken.model.RESTBesluit;
import net.atos.zac.app.zaken.model.RESTBesluitToevoegenGegevens;
import net.atos.zac.configuratie.ConfiguratieService;

public class RESTBesluitConverter {

    @Inject
    private RESTBesluittypeConverter restBesluittypeConverter;

    public RESTBesluit convertToRESTBesluit(final Besluit besluit) {
        if (besluit != null) {
            final RESTBesluit restBesluit = new RESTBesluit();
            restBesluit.besluittype = restBesluittypeConverter.convertToRESTBesluittype(besluit.getBesluittype());
            restBesluit.datum = besluit.getDatum();
            restBesluit.identificatie = besluit.getIdentificatie();
            restBesluit.url = besluit.getUrl();
            restBesluit.toelichting = besluit.getToelichting();
            restBesluit.ingangsdatum = besluit.getIngangsdatum();
            restBesluit.vervaldatum = besluit.getVervaldatum();
            return restBesluit;
        }
        return null;
    }

    public Besluit convertToBesluit(final RESTBesluitToevoegenGegevens besluitToevoegenGegevens) {
        final Besluit besluit = new Besluit();
        besluit.setBesluittype(besluitToevoegenGegevens.besluittypeURL);
        besluit.setDatum(LocalDate.now());
        besluit.setIngangsdatum(besluitToevoegenGegevens.ingangsdatum);
        besluit.setVervaldatum(besluitToevoegenGegevens.vervaldatum);
        besluit.setVerantwoordelijkeOrganisatie(ConfiguratieService.VERANTWOORDELIJKE_ORGANISATIE);
        besluit.setToelichting(besluitToevoegenGegevens.toelichting);
        return besluit;
    }
}
