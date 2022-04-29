/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken.converter;

import java.util.UUID;

import net.atos.zac.app.shared.RESTResult;
import net.atos.zac.app.zoeken.model.RESTZaakZoekItem;
import net.atos.zac.zoeken.model.ZaakZoekItem;
import net.atos.zac.zoeken.model.ZoekResultaat;

public class RESTZoekZaakResultaatConverter {

    public RESTResult<RESTZaakZoekItem> convert(final ZoekResultaat<ZaakZoekItem> zoekResultaat) {
        return new RESTResult<>(zoekResultaat.getItems().stream().map(this::convert).toList(), zoekResultaat.getCount());
    }

    private RESTZaakZoekItem convert(final ZaakZoekItem zoekItem) {
        final RESTZaakZoekItem restZoekItem = new RESTZaakZoekItem();
        restZoekItem.uuid = UUID.fromString(zoekItem.getUuid());
        restZoekItem.identificatie = zoekItem.getIdentificatie();
        restZoekItem.zaaktypeNaam = zoekItem.getZaaktypeOmschrijving();
        restZoekItem.statusNaam = zoekItem.getStatusNaam();
        restZoekItem.behandelaarNaam = zoekItem.getBehandelaarNaam();
        restZoekItem.groepNaam = zoekItem.getGroepNaam();
        restZoekItem.omschrijving = zoekItem.getOmschrijving();
        restZoekItem.toelichting = zoekItem.getToelichting();
        return restZoekItem;
    }
}
