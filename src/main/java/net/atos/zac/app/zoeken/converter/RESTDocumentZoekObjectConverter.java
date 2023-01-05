/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken.converter;

import java.util.EnumSet;
import java.util.stream.Collectors;

import javax.inject.Inject;

import net.atos.zac.app.policy.converter.RESTRechtenConverter;
import net.atos.zac.app.zoeken.model.RESTDocumentZoekObject;
import net.atos.zac.policy.PolicyService;
import net.atos.zac.util.DateTimeConverterUtil;
import net.atos.zac.zoeken.model.DocumentIndicatie;
import net.atos.zac.zoeken.model.zoekobject.DocumentZoekObject;

public class RESTDocumentZoekObjectConverter {

    @Inject
    private PolicyService policyService;

    @Inject
    private RESTRechtenConverter restRechtenConverter;

    public RESTDocumentZoekObject convert(final DocumentZoekObject documentZoekObject) {
        final RESTDocumentZoekObject restDocumentZoekObject = new RESTDocumentZoekObject();
        restDocumentZoekObject.id = documentZoekObject.getUuid();
        restDocumentZoekObject.type = documentZoekObject.getType();
        restDocumentZoekObject.titel = documentZoekObject.getTitel();
        restDocumentZoekObject.beschrijving = documentZoekObject.getBeschrijving();
        restDocumentZoekObject.zaaktypeUuid = documentZoekObject.getZaaktypeUuid();
        restDocumentZoekObject.zaaktypeIdentificatie = documentZoekObject.getZaaktypeIdentificatie();
        restDocumentZoekObject.zaaktypeOmschrijving = documentZoekObject.getZaaktypeOmschrijving();
        restDocumentZoekObject.zaaktypeDomein = documentZoekObject.getZaaktypeDomein();
        restDocumentZoekObject.zaakIdentificatie = documentZoekObject.getZaakIdentificatie();
        restDocumentZoekObject.zaakUuid = documentZoekObject.getZaakUuid();
        restDocumentZoekObject.zaakRelatie = documentZoekObject.getZaakRelatie();
        restDocumentZoekObject.creatiedatum = DateTimeConverterUtil.convertToLocalDate(documentZoekObject.getCreatiedatum());
        restDocumentZoekObject.registratiedatum = DateTimeConverterUtil.convertToLocalDate(documentZoekObject.getRegistratiedatum());
        restDocumentZoekObject.ontvangstdatum = DateTimeConverterUtil.convertToLocalDate(documentZoekObject.getOntvangstdatum());
        restDocumentZoekObject.verzenddatum = DateTimeConverterUtil.convertToLocalDate(documentZoekObject.getVerzenddatum());
        restDocumentZoekObject.ondertekeningDatum = DateTimeConverterUtil.convertToLocalDate(documentZoekObject.getOndertekeningDatum());
        restDocumentZoekObject.vertrouwelijkheidaanduiding = documentZoekObject.getVertrouwelijkheidaanduiding();
        restDocumentZoekObject.auteur = documentZoekObject.getAuteur();
        restDocumentZoekObject.status = documentZoekObject.getStatus().toString();
        restDocumentZoekObject.formaat = documentZoekObject.getFormaat();
        restDocumentZoekObject.versie = documentZoekObject.getVersie();
        restDocumentZoekObject.bestandsnaam = documentZoekObject.getBestandsnaam();
        restDocumentZoekObject.bestandsomvang = documentZoekObject.getBestandsomvang();
        restDocumentZoekObject.documentType = documentZoekObject.getDocumentType();
        restDocumentZoekObject.ondertekeningSoort = documentZoekObject.getOndertekeningSoort();
        restDocumentZoekObject.indicatieOndertekend = documentZoekObject.isIndicatie(DocumentIndicatie.ONDERTEKEND);
        restDocumentZoekObject.inhoudUrl = documentZoekObject.getInhoudUrl();
        restDocumentZoekObject.indicatieVergrendeld = documentZoekObject.isIndicatie(DocumentIndicatie.VERGRENDELD);
        restDocumentZoekObject.vergrendeldDoor = documentZoekObject.getVergrendeldDoorNaam();
        restDocumentZoekObject.indicaties = documentZoekObject.getDocumentIndicaties().stream()
                .filter(indicatie -> !indicatie.equals(DocumentIndicatie.GEBRUIKSRECHT))
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(DocumentIndicatie.class)));
        restDocumentZoekObject.rechten = restRechtenConverter.convert(policyService.readDocumentRechten(documentZoekObject));
        return restDocumentZoekObject;
    }
}
