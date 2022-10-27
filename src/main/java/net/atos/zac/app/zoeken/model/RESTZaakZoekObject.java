/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken.model;

import java.time.LocalDate;

import net.atos.zac.app.policy.model.RESTZaakRechten;

public class RESTZaakZoekObject extends AbstractRESTZoekObject {

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

    public String groepNaam;

    public String behandelaarNaam;

    public String behandelaarGebruikersnaam;

    public String initiatorIdentificatie;

    public String locatie;

    public boolean indicatieVerlenging;

    public boolean indicatieOpschorting;

    public boolean indicatieHeropend;

    public boolean indicatieDeelzaak;

    public boolean indicatieHoofdzaak;

    public String duurVerlenging;

    public String redenVerlenging;

    public String redenOpschorting;

    public String zaaktypeUuid;

    public String zaaktypeOmschrijving;

    public String resultaattypeOmschrijving;

    public String resultaatToelichting;

    public String statustypeOmschrijving;

    public String statusToelichting;

    public long aantalOpenstaandeTaken;

    public RESTZaakRechten rechten;

}
