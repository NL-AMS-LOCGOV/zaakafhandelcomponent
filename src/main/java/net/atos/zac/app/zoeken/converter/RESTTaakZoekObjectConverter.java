/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken.converter;

import java.util.UUID;

import net.atos.zac.app.zoeken.model.RESTTaakZoekObject;
import net.atos.zac.util.DateTimeConverterUtil;
import net.atos.zac.zoeken.model.TaakZoekObject;

public class RESTTaakZoekObjectConverter {

    public static RESTTaakZoekObject convert(final TaakZoekObject taakZoekObject) {
        final RESTTaakZoekObject restZaakZoekObject = new RESTTaakZoekObject();
        restZaakZoekObject.uuid = UUID.fromString(taakZoekObject.getUuid());
        restZaakZoekObject.type = taakZoekObject.getType();
        restZaakZoekObject.identificatie = taakZoekObject.getIdentificatie();
        restZaakZoekObject.naam = taakZoekObject.getNaam();
        restZaakZoekObject.status = taakZoekObject.getStatus();
        restZaakZoekObject.toelichting = taakZoekObject.getToelichting();
        restZaakZoekObject.creatiedatum = DateTimeConverterUtil.convertToLocalDate(taakZoekObject.getCreatiedatum());
        restZaakZoekObject.toekenningsdatum = DateTimeConverterUtil.convertToLocalDate(taakZoekObject.getToekenningsdatum());
        restZaakZoekObject.streefdatum = DateTimeConverterUtil.convertToLocalDate(taakZoekObject.getStreefdatum());
        restZaakZoekObject.groepNaam = taakZoekObject.getGroepNaam();
        restZaakZoekObject.behandelaarNaam = taakZoekObject.getBehandelaarNaam();
        restZaakZoekObject.behandelaarGebruikersnaam = taakZoekObject.getBehandelaarGebruikersnaam();
        restZaakZoekObject.zaaktypeOmschrijving = taakZoekObject.getZaaktypeOmschrijving();
        restZaakZoekObject.zaakIdentificatie = taakZoekObject.getZaakIdentificatie();
        return restZaakZoekObject;
    }
}
