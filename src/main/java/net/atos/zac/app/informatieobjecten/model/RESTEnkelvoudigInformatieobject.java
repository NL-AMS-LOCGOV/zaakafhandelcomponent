/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten.model;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

import net.atos.zac.app.identity.model.RESTUser;

/**
 *
 */
public class RESTEnkelvoudigInformatieobject {

    public String uuid;

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

    public String status;

    public String formaat;

    public String taal;

    public Integer versie;

    public UUID informatieobjectTypeUUID;

    public String informatieobjectTypeOmschrijving;

    public String bestandsnaam;

    public Long bestandsomvang;

    public String link;

    public boolean indicatieGebruiksrecht;

    public RESTUser gelockedDoor;

    public RESTEnkelvoudigInformatieobjectActies acties;
}
