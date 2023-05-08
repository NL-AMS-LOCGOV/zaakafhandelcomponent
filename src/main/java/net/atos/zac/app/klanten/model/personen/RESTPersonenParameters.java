/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.klanten.model.personen;

public class RESTPersonenParameters {

    public Cardinaliteit bsn;

    public Cardinaliteit geslachtsnaam;

    public Cardinaliteit voornamen;

    public Cardinaliteit voorvoegsel;

    public Cardinaliteit geboortedatum;

    public Cardinaliteit gemeenteVanInschrijving;

    public Cardinaliteit postcode;

    public Cardinaliteit huisnummer;

    public Cardinaliteit straat;

    public RESTPersonenParameters(final Cardinaliteit bsn,
            final Cardinaliteit geslachtsnaam, final Cardinaliteit voornamen, final Cardinaliteit voorvoegsel,
            final Cardinaliteit geboortedatum,
            final Cardinaliteit gemeenteVanInschrijving,
            final Cardinaliteit postcode, final Cardinaliteit huisnummer, final Cardinaliteit straat) {
        this.bsn = bsn;
        this.geslachtsnaam = geslachtsnaam;
        this.voornamen = voornamen;
        this.voorvoegsel = voorvoegsel;
        this.geboortedatum = geboortedatum;
        this.gemeenteVanInschrijving = gemeenteVanInschrijving;
        this.postcode = postcode;
        this.huisnummer = huisnummer;
        this.straat = straat;
    }

    public enum Cardinaliteit {
        NON, // niet beschikbaar
        OPT, // optioneel
        REQ // verplicht
    }
}
