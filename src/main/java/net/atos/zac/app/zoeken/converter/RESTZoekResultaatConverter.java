/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.atos.zac.app.zoeken.model.RESTZaakZoekObject;
import net.atos.zac.app.zoeken.model.RESTZoekParameters;
import net.atos.zac.app.zoeken.model.RESTZoekResultaat;
import net.atos.zac.util.DateTimeConverterUtil;
import net.atos.zac.zoeken.model.ZaakZoekObject;
import net.atos.zac.zoeken.model.ZoekResultaat;

public class RESTZoekResultaatConverter {

    public RESTZoekResultaat<RESTZaakZoekObject> convert(final ZoekResultaat<ZaakZoekObject> zoekResultaat, final RESTZoekParameters zoekParameters) {
        final RESTZoekResultaat<RESTZaakZoekObject> restZoekResultaat =
                new RESTZoekResultaat<>(zoekResultaat.getItems().stream().map(this::convert).toList(), zoekResultaat.getCount());
        restZoekResultaat.filters.putAll(zoekResultaat.getFilters());
        zoekResultaat.getFilters().forEach((filterVeld, mogelijkeFilters) -> {
            //indien geen resultaten, de huidige filters laten staan
            final String zoekFilter = zoekParameters.filters.get(filterVeld);
            if (zoekFilter != null && !mogelijkeFilters.contains(zoekFilter)) {
                final List<String> filters = new ArrayList<>(mogelijkeFilters);
                filters.add(zoekFilter);
                restZoekResultaat.filters.put(filterVeld, filters);
            }
        });
        return restZoekResultaat;
    }

    private RESTZaakZoekObject convert(final ZaakZoekObject zoekItem) {
        final RESTZaakZoekObject restZoekItem = new RESTZaakZoekObject();
        restZoekItem.uuid = UUID.fromString(zoekItem.getUuid());
        restZoekItem.type = zoekItem.getType();
        restZoekItem.identificatie = zoekItem.getIdentificatie();
        restZoekItem.omschrijving = zoekItem.getOmschrijving();
        restZoekItem.toelichting = zoekItem.getToelichting();
        restZoekItem.registratiedatum = DateTimeConverterUtil.convertToLocalDate(zoekItem.getRegistratiedatum());
        restZoekItem.startdatum = DateTimeConverterUtil.convertToLocalDate(zoekItem.getStartdatum());
        restZoekItem.einddatum = DateTimeConverterUtil.convertToLocalDate(zoekItem.getEinddatum());
        restZoekItem.einddatumGepland = DateTimeConverterUtil.convertToLocalDate(zoekItem.getEinddatumGepland());
        restZoekItem.uiterlijkeEinddatumAfdoening = DateTimeConverterUtil.convertToLocalDate(zoekItem.getUiterlijkeEinddatumAfdoening());
        restZoekItem.publicatiedatum = DateTimeConverterUtil.convertToLocalDate(zoekItem.getPublicatiedatum());
        restZoekItem.communicatiekanaal = zoekItem.getCommunicatiekanaal();
        restZoekItem.vertrouwelijkheidaanduiding = zoekItem.getVertrouwelijkheidaanduiding();
        restZoekItem.afgehandeld = zoekItem.isAfgehandeld();
        restZoekItem.groepNaam = zoekItem.getGroepNaam();
        restZoekItem.behandelaarNaam = zoekItem.getBehandelaarNaam();
        restZoekItem.behandelaarGebruikersnaam = zoekItem.getBehandelaarGebruikersnaam();
        restZoekItem.initiatorIdentificatie = zoekItem.getInitiatorIdentificatie();
        restZoekItem.zaaktypeOmschrijving = zoekItem.getZaaktypeOmschrijving();
        restZoekItem.statustypeOmschrijving = zoekItem.getStatustypeOmschrijving();
        restZoekItem.resultaattypeOmschrijving = zoekItem.getResultaattypeOmschrijving();
        restZoekItem.aantalOpenstaandeTaken = zoekItem.getAantalOpenstaandeTaken();
        return restZoekItem;
    }
}
