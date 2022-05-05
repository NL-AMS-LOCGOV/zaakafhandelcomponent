/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken.converter;

import java.util.UUID;

import net.atos.zac.app.shared.RESTResult;
import net.atos.zac.app.zoeken.model.RESTZaakZoekObject;
import net.atos.zac.util.DateTimeConverterUtil;
import net.atos.zac.zoeken.model.ZaakZoekObject;
import net.atos.zac.zoeken.model.ZoekResultaat;

public class RESTSolrZoekResultaatConverter {

    public RESTResult<RESTZaakZoekObject> convert(final ZoekResultaat<ZaakZoekObject> zoekResultaat) {
        return new RESTResult<>(zoekResultaat.getItems().stream().map(this::convert).toList(), zoekResultaat.getCount());
    }

    private RESTZaakZoekObject convert(final ZaakZoekObject zoekItem) {
        final RESTZaakZoekObject restZoekItem = new RESTZaakZoekObject();
        restZoekItem.type = zoekItem.getType();
        restZoekItem.uuid = UUID.fromString(zoekItem.getUuid());
        restZoekItem.identificatie = zoekItem.getIdentificatie();
        restZoekItem.startdatum = DateTimeConverterUtil.convertToLocalDate(zoekItem.getStartdatum());
        restZoekItem.einddatum = DateTimeConverterUtil.convertToLocalDate(zoekItem.getEinddatum());
        restZoekItem.registratiedatum = DateTimeConverterUtil.convertToLocalDate(zoekItem.getRegistratiedatum());
        restZoekItem.streefdatum = DateTimeConverterUtil.convertToLocalDate(zoekItem.getStreefdatum());
        restZoekItem.fataledatum = DateTimeConverterUtil.convertToLocalDate(zoekItem.getFataledatum());
        restZoekItem.zaaktypeNaam = zoekItem.getZaaktypeOmschrijving();
        restZoekItem.statusNaam = zoekItem.getStatusNaam();
        restZoekItem.resultaatNaam = zoekItem.getResultaatNaam();
        restZoekItem.behandelaarNaam = zoekItem.getBehandelaarNaam();
        restZoekItem.groepNaam = zoekItem.getGroepNaam();
        restZoekItem.omschrijving = zoekItem.getOmschrijving();
        restZoekItem.toelichting = zoekItem.getToelichting();
        return restZoekItem;
    }
}
