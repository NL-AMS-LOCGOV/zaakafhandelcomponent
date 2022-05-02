/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken.converter;

import java.util.List;

import net.atos.zac.app.zoeken.model.RESTZoekZaakResultaat;
import net.atos.zac.zoeken.model.ZoekZaakResultaat;

public class RESTZoekZaakResultaatConverter {

    public List<RESTZoekZaakResultaat> convert(final List<ZoekZaakResultaat> zoekZaakResultaten) {
        return zoekZaakResultaten.stream().map(this::convert).toList();
    }

    private RESTZoekZaakResultaat convert(final ZoekZaakResultaat zoekZaakResultaat) {
        final RESTZoekZaakResultaat restZoekZaakResultaat = new RESTZoekZaakResultaat();
        restZoekZaakResultaat.uuid = zoekZaakResultaat.getUuid();
        restZoekZaakResultaat.identificatie = zoekZaakResultaat.getIdentificatie();
        restZoekZaakResultaat.zaaktype = zoekZaakResultaat.getZaaktype();
        restZoekZaakResultaat.status = zoekZaakResultaat.getStatus();
        restZoekZaakResultaat.omschrijving = zoekZaakResultaat.getOmschrijving();
        restZoekZaakResultaat.toelichting = zoekZaakResultaat.getToelichting();
        restZoekZaakResultaat.locatie = zoekZaakResultaat.getLocatie();
        return restZoekZaakResultaat;
    }
}
