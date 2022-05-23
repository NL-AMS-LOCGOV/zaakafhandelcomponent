/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten.model;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 *
 */
public class RESTEnkelvoudigInformatieobject {
    public String zaakRelatie;

    public String uuid;

    public String url;

    public String identificatie;

    public String bronorganisatie;

    public LocalDate creatiedatum;

    public String titel;

    public String vertrouwelijkheidaanduiding;

    public String auteur;

    public String status;

    public String formaat;

    public String taal;

    public Integer versie;

    public ZonedDateTime registratiedatumTijd;

    public String bestandsnaam;

    public Long bestandsomvang;

    public String link;

    public UUID informatieobjectTypeUUID;

    public String informatieobjectTypeOmschrijving;

    public String beschrijving;

    public LocalDate ontvangstdatum;

    public LocalDate verzenddatum;

    public boolean indicatieGebruiksrecht;

    public LocalDate ondertekening;

    public boolean locked;

    public boolean startformulier;

    public String inhoudUrl;

    public boolean taakObject;
}
