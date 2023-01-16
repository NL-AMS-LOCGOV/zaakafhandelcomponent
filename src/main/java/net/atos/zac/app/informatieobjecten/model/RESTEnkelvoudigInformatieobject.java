/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten.model;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.EnumSet;
import java.util.UUID;

import net.atos.client.zgw.drc.model.InformatieobjectStatus;
import net.atos.zac.app.identity.model.RESTUser;
import net.atos.zac.app.policy.model.RESTDocumentRechten;
import net.atos.zac.zoeken.model.DocumentIndicatie;

/**
 *
 */
public class RESTEnkelvoudigInformatieobject {

    public UUID uuid;

    public String identificatie;

    public String titel;

    public String beschrijving;

    public LocalDate creatiedatum;

    public ZonedDateTime registratiedatumTijd;

    public LocalDate ontvangstdatum;

    public LocalDate verzenddatum;

    public String bronorganisatie;

    public String vertrouwelijkheidaanduiding;

    public String auteur;

    public InformatieobjectStatus status;

    public String formaat;

    public String taal;

    public Integer versie;

    public UUID informatieobjectTypeUUID;

    public String informatieobjectTypeOmschrijving;

    public String bestandsnaam;

    public Long bestandsomvang;

    public String link;

    public RESTOndertekening ondertekening;

    public boolean indicatieGebruiksrecht;

    public EnumSet<DocumentIndicatie> getIndicaties() {
        final EnumSet<DocumentIndicatie> indicaties = EnumSet.noneOf(DocumentIndicatie.class);
        if (gelockedDoor != null) {
            indicaties.add(DocumentIndicatie.VERGRENDELD);
        }
        if (ondertekening != null) {
            indicaties.add(DocumentIndicatie.ONDERTEKEND);
        }
        if (indicatieGebruiksrecht) {
            indicaties.add(DocumentIndicatie.GEBRUIKSRECHT);
        }
        if (isBesluitDocument) {
            indicaties.add(DocumentIndicatie.BESLUIT);
        }
        return indicaties;
    }

    public RESTUser gelockedDoor;

    public boolean isBesluitDocument;

    public RESTDocumentRechten rechten;
}
