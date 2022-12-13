/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken.model;

import java.time.LocalDate;
import java.util.EnumSet;

import net.atos.zac.app.policy.model.RESTDocumentRechten;
import net.atos.zac.zoeken.model.DocumentIndicatie;

public class RESTDocumentZoekObject extends AbstractRESTZoekObject {

    public String titel;

    public String beschrijving;

    public String zaaktypeUuid;

    public String zaaktypeIdentificatie;

    public String zaaktypeOmschrijving;

    public String zaakIdentificatie;

    public String zaakUuid;

    public String zaakRelatie;

    public LocalDate creatiedatum;

    public LocalDate registratiedatum;

    public LocalDate ontvangstdatum;

    public LocalDate verzenddatum;

    public LocalDate ondertekeningDatum;

    public String vertrouwelijkheidaanduiding;

    public String auteur;

    public String status;

    public String formaat;

    public long versie;

    public String bestandsnaam;

    public long bestandsomvang;

    public String documentType;

    public String ondertekeningSoort;

    public boolean indicatieOndertekend;

    public String inhoudUrl;

    public boolean indicatieVergrendeld;

    public String vergrendeldDoor;

    public EnumSet<DocumentIndicatie> indicaties;

    public RESTDocumentRechten rechten;

    public boolean indicatieGebruiksrecht;

}
