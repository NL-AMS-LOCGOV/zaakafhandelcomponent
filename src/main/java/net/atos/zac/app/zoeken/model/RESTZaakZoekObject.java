/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken.model;

import java.time.LocalDate;
import java.util.UUID;

public class RESTZaakZoekObject {

    public UUID uuid;

    public String type;

    public String identificatie;

    public String omschrijving;

    public String toelichting;

    public LocalDate registratiedatum;

    public LocalDate startdatum;

    public LocalDate einddatumGepland;

    public LocalDate einddatum;

    public LocalDate uiterlijkeEinddatumAfdoening;

    public LocalDate publicatiedatum;

    public String communicatiekanaal;

    public String vertrouwelijkheidaanduiding;

    public boolean afgehandeld;

    public String groepID;

    public String groepNaam;

    public String behandelaarNaam;

    public String behandelaarGebruikersnaam;

    public String initiatorIdentificatie;

    public String locatie;

    public boolean indicatieVerlenging;

    public String duurVerlenging;

    public String redenVerlenging;

    public String redenOpschorting;

    public String zaaktypeUuid;

    public String zaaktypeIdentificatie;

    public String zaaktypeOmschrijving;

    public String resultaattypeOmschrijving;

    public String resultaatToelichting;

    public boolean statusEindstatus;

    public String statustypeOmschrijving;

    public LocalDate statusDatumGezet;

    public String statusToelichting;

    public long aantalOpenstaandeTaken;

}
