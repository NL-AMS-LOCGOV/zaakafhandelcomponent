/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten.model;

import java.time.LocalDate;
import java.time.ZonedDateTime;

/**
 *
 */
public class RESTInformatieObject {
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

    public String informatieobjectType;

    public String documentType;

    public String beschrijving;

    public LocalDate ontvangstdatum;

    public LocalDate verzenddatum;

    public boolean indicatieGebruiksrecht;

    public String ondertekening;

    //public URI informatieobjecttype;
    public boolean locked;

    public String inhoudUrl;
}
