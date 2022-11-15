/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken.converter;

import java.util.UUID;

import javax.inject.Inject;

import net.atos.zac.app.policy.converter.RESTRechtenConverter;
import net.atos.zac.app.zoeken.model.RESTDocumentZoekObject;
import net.atos.zac.policy.PolicyService;
import net.atos.zac.zoeken.model.zoekobject.DocumentZoekObject;

public class RESTDocumentZoekObjectConverter {

    @Inject
    private PolicyService policyService;

    @Inject
    private RESTRechtenConverter restRechtenConverter;

    public RESTDocumentZoekObject convert(final DocumentZoekObject documentZoekObject) {
        final RESTDocumentZoekObject restDocumentZoekObject = new RESTDocumentZoekObject();
        restDocumentZoekObject.id = UUID.fromString(documentZoekObject.getUuid());
        restDocumentZoekObject.type = documentZoekObject.getType();
        restDocumentZoekObject.titel = documentZoekObject.getTitel();
        restDocumentZoekObject.beschrijving = documentZoekObject.getBeschrijving();
        restDocumentZoekObject.zaaktypeUuid = documentZoekObject.getZaaktypeUuid();
        restDocumentZoekObject.zaaktypeIdentificatie = documentZoekObject.getZaaktypeIdentificatie();
        restDocumentZoekObject.zaaktypeOmschrijving = documentZoekObject.getZaaktypeOmschrijving();
        restDocumentZoekObject.zaakId = documentZoekObject.getZaakId();
        restDocumentZoekObject.zaakUuid = documentZoekObject.getZaakUuid();
        restDocumentZoekObject.zaakRelatie = documentZoekObject.getZaakRelatie();
        restDocumentZoekObject.creatiedatum = documentZoekObject.getCreatiedatum();
        restDocumentZoekObject.registratiedatum = documentZoekObject.getRegistratiedatum();
        restDocumentZoekObject.ontvangstdatum = documentZoekObject.getOntvangstdatum();
        restDocumentZoekObject.verzenddatum = documentZoekObject.getVerzenddatum();
        restDocumentZoekObject.ondertekeningDatum = documentZoekObject.getOndertekeningDatum();
        restDocumentZoekObject.vertrouwelijkheidaanduiding = documentZoekObject.getVertrouwelijkheidaanduiding();
        restDocumentZoekObject.auteur = documentZoekObject.getAuteur();
        restDocumentZoekObject.status = documentZoekObject.getStatus();
        restDocumentZoekObject.formaat = documentZoekObject.getFormaat();
        restDocumentZoekObject.versie = documentZoekObject.getVersie();
        restDocumentZoekObject.bestandsnaam = documentZoekObject.getBestandsnaam();
        restDocumentZoekObject.bestandsomvang = documentZoekObject.getBestandsomvang();
        restDocumentZoekObject.documentType = documentZoekObject.getDocumentType();
        restDocumentZoekObject.ondertekeningSoort = documentZoekObject.getOndertekeningSoort();
        restDocumentZoekObject.indicatieOndertekend = documentZoekObject.isIndicatieOndertekend();
        restDocumentZoekObject.inhoudUrl = documentZoekObject.getInhoudUrl();
        restDocumentZoekObject.indicatieVergrendeld = documentZoekObject.isIndicatieVergrendeld();
        restDocumentZoekObject.vergrendeldDoor = documentZoekObject.getVergrendeldDoorNaam();
        restDocumentZoekObject.rechten = restRechtenConverter.convert(policyService.readDocumentRechten(documentZoekObject));
        return restDocumentZoekObject;
    }
}
