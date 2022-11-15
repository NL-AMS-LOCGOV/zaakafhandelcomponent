/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken.model;

import java.util.Date;

import net.atos.zac.app.policy.model.RESTDocumentRechten;

public class RESTDocumentZoekObject extends AbstractRESTZoekObject {

    public String titel;

    public String beschrijving;

    public String zaaktypeUuid;

    public String zaaktypeIdentificatie;

    public String zaaktypeOmschrijving;

    public String zaakId;

    public String zaakUuid;

    public String zaakRelatie;

    public Date creatiedatum;

    public Date registratiedatum;

    public Date ontvangstdatum;

    public Date verzenddatum;

    public Date ondertekeningDatum;

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

    public RESTDocumentRechten rechten;

}
