/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

public class NatuurlijkPersoon {

    /**
     * Het burgerservicenummer, bedoeld in artikel 1.1 van de Wet algemene bepalingen burgerservicenummer.
     * - maxLength: 9
     */
    private String inpBsn;

    /**
     * Het door de gemeente uitgegeven unieke nummer voor een ANDER NATUURLIJK PERSOON
     * - maxLength: 17
     */
    private String anpIdentificatie;

    /**
     * Het administratienummer van de persoon, bedoeld in de Wet BRP
     * - pattern: ^[1-9][0-9]{9}$
     * - maxlength: 10
     */
    private String inpA_nummer;

    /**
     * De stam van de geslachtsnaam.
     * - maxLength: 200
     */
    private String geslachtsnaam;

    /**
     * Voorvoegsel geslachtsnaam
     * - maxLength: 80
     */
    private String voorvoegselGeslachtsnaam;

    /**
     * De verzameling letters die gevormd wordt door de eerste letter van alle in volgorde voorkomende voornamen.
     * - maxLength: 20
     */
    private String voorletters;

    /**
     * Voornamen bij de naam die de persoon wenst te voeren.
     * - maxLength: 200
     */
    private String voornamen;

    /**
     * Een aanduiding die aangeeft of de persoon een man of een vrouw is, of dat het geslacht nog onbekend is
     */
    private Geslachtsaanduiding geslachtsaanduiding;

    /**
     * - maxLength: 18
     */
    private String geboortedatum;

    /**
     *
     */
    private VerblijfsAdres verblijfsadres;

    /**
     *
     */
    private SubVerblijfBuitenland subVerblijfBuitenland;

    public String getInpBsn() {
        return inpBsn;
    }

    public void setInpBsn(final String inpBsn) {
        this.inpBsn = inpBsn;
    }

    public String getAnpIdentificatie() {
        return anpIdentificatie;
    }

    public void setAnpIdentificatie(final String anpIdentificatie) {
        this.anpIdentificatie = anpIdentificatie;
    }

    public String getInpA_nummer() {
        return inpA_nummer;
    }

    public void setInpA_nummer(final String inpA_nummer) {
        this.inpA_nummer = inpA_nummer;
    }

    public String getGeslachtsnaam() {
        return geslachtsnaam;
    }

    public void setGeslachtsnaam(final String geslachtsnaam) {
        this.geslachtsnaam = geslachtsnaam;
    }

    public String getVoorvoegselGeslachtsnaam() {
        return voorvoegselGeslachtsnaam;
    }

    public void setVoorvoegselGeslachtsnaam(final String voorvoegselGeslachtsnaam) {
        this.voorvoegselGeslachtsnaam = voorvoegselGeslachtsnaam;
    }

    public String getVoorletters() {
        return voorletters;
    }

    public void setVoorletters(final String voorletters) {
        this.voorletters = voorletters;
    }

    public String getVoornamen() {
        return voornamen;
    }

    public void setVoornamen(final String voornamen) {
        this.voornamen = voornamen;
    }

    public Geslachtsaanduiding getGeslachtsaanduiding() {
        return geslachtsaanduiding;
    }

    public void setGeslachtsaanduiding(final Geslachtsaanduiding geslachtsaanduiding) {
        this.geslachtsaanduiding = geslachtsaanduiding;
    }

    public String getGeboortedatum() {
        return geboortedatum;
    }

    public void setGeboortedatum(final String geboortedatum) {
        this.geboortedatum = geboortedatum;
    }

    public VerblijfsAdres getVerblijfsadres() {
        return verblijfsadres;
    }

    public void setVerblijfsadres(final VerblijfsAdres verblijfsadres) {
        this.verblijfsadres = verblijfsadres;
    }

    public SubVerblijfBuitenland getSubVerblijfBuitenland() {
        return subVerblijfBuitenland;
    }

    public void setSubVerblijfBuitenland(final SubVerblijfBuitenland subVerblijfBuitenland) {
        this.subVerblijfBuitenland = subVerblijfBuitenland;
    }
}
