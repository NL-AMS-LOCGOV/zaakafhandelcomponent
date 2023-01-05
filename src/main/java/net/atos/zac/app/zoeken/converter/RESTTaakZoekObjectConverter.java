/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken.converter;

import javax.inject.Inject;

import net.atos.zac.app.policy.converter.RESTRechtenConverter;
import net.atos.zac.app.zoeken.model.RESTTaakZoekObject;
import net.atos.zac.policy.PolicyService;
import net.atos.zac.util.DateTimeConverterUtil;
import net.atos.zac.zoeken.model.zoekobject.TaakZoekObject;

public class RESTTaakZoekObjectConverter {

    @Inject
    private PolicyService policyService;

    @Inject
    private RESTRechtenConverter restRechtenConverter;

    public RESTTaakZoekObject convert(final TaakZoekObject taakZoekObject) {
        final RESTTaakZoekObject restTaakZoekObject = new RESTTaakZoekObject();
        restTaakZoekObject.id = taakZoekObject.getId();
        restTaakZoekObject.type = taakZoekObject.getType();
        restTaakZoekObject.naam = taakZoekObject.getNaam();
        restTaakZoekObject.status = taakZoekObject.getStatus();
        restTaakZoekObject.toelichting = taakZoekObject.getToelichting();
        restTaakZoekObject.creatiedatum = DateTimeConverterUtil.convertToLocalDate(taakZoekObject.getCreatiedatum());
        restTaakZoekObject.toekenningsdatum = DateTimeConverterUtil.convertToLocalDate(
                taakZoekObject.getToekenningsdatum());
        restTaakZoekObject.fataledatum = DateTimeConverterUtil.convertToLocalDate(taakZoekObject.getFataledatum());
        restTaakZoekObject.groepNaam = taakZoekObject.getGroepNaam();
        restTaakZoekObject.behandelaarNaam = taakZoekObject.getBehandelaarNaam();
        restTaakZoekObject.behandelaarGebruikersnaam = taakZoekObject.getBehandelaarGebruikersnaam();
        restTaakZoekObject.zaaktypeOmschrijving = taakZoekObject.getZaaktypeOmschrijving();
        restTaakZoekObject.zaaktypeDomein = taakZoekObject.getZaaktypeDomein();
        restTaakZoekObject.zaakIdentificatie = taakZoekObject.getZaakIdentificatie();
        restTaakZoekObject.zaakUuid = taakZoekObject.getZaakUUID();
        restTaakZoekObject.zaakToelichting = taakZoekObject.getZaakToelichting();
        restTaakZoekObject.zaakOmschrijving = taakZoekObject.getZaakOmschrijving();
        restTaakZoekObject.rechten = restRechtenConverter.convert(policyService.readTaakRechten(taakZoekObject));
        return restTaakZoekObject;
    }
}
