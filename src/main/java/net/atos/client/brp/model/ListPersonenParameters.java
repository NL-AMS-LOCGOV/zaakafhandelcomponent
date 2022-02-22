/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.brp.model;

import java.time.LocalDate;
import java.util.List;

import javax.ws.rs.QueryParam;

public class ListPersonenParameters {

    @QueryParam("fields")
    private String fields;

    @QueryParam("burgerservicenummer")
    private List<String> burgerservicenummers;

    @QueryParam("geboorte__datum")
    private LocalDate geboorteDatum;

    @QueryParam("geboorte__plaats")
    private String geboortePlaats;

    @QueryParam("geslachtsaanduiding")
    private GeslachtEnum geslachtsaanduiding;

    @QueryParam("inclusiefOverledenPersonen")
    private Boolean inclusiefOverledenPersonen;

    @QueryParam("naam__geslachtsnaam")
    private String naamGeslachtsnaam;

    @QueryParam("naam__voorvoegsel")
    private String naamVoorvoegsel;

    @QueryParam("naam__voornamen")
    private String naamVoornamen;

    @QueryParam("verblijfplaats__gemeenteVanInschrijving")
    private String verblijfplaatsGemeenteVanInschrijving;

    @QueryParam("verblijfplaats__huisletter")
    private String verblijfplaatsHuisletter;

    @QueryParam("verblijfplaats__huisnummer")
    private Integer verblijfplaatsHuisnummer;

    @QueryParam("verblijfplaats__huisnummertoevoeging")
    private String verblijfplaatsHuisnummertoevoeging;

    @QueryParam("verblijfplaats__nummeraanduidingIdentificatie")
    private String verblijfplaatsNummeraanduidingIdentificatie;

    @QueryParam("verblijfplaats__straat")
    private String verblijfplaatsStraat;

    @QueryParam("verblijfplaats__postcode")
    private String verblijfplaatsPostcode;

    public String getFields() {
        return fields;
    }

    public void setFields(final String fields) {
        this.fields = fields;
    }

    public List<String> getBurgerservicenummers() {
        return burgerservicenummers;
    }

    public void setBurgerservicenummers(final List<String> burgerservicenummers) {
        this.burgerservicenummers = burgerservicenummers;
    }

    public LocalDate getGeboorteDatum() {
        return geboorteDatum;
    }

    public void setGeboorteDatum(final LocalDate geboorteDatum) {
        this.geboorteDatum = geboorteDatum;
    }

    public String getGeboortePlaats() {
        return geboortePlaats;
    }

    public void setGeboortePlaats(final String geboortePlaats) {
        this.geboortePlaats = geboortePlaats;
    }

    public GeslachtEnum getGeslachtsaanduiding() {
        return geslachtsaanduiding;
    }

    public void setGeslachtsaanduiding(final GeslachtEnum geslachtsaanduiding) {
        this.geslachtsaanduiding = geslachtsaanduiding;
    }

    public Boolean getInclusiefOverledenPersonen() {
        return inclusiefOverledenPersonen;
    }

    public void setInclusiefOverledenPersonen(final Boolean inclusiefOverledenPersonen) {
        this.inclusiefOverledenPersonen = inclusiefOverledenPersonen;
    }

    public String getNaamGeslachtsnaam() {
        return naamGeslachtsnaam;
    }

    public void setNaamGeslachtsnaam(final String naamGeslachtsnaam) {
        this.naamGeslachtsnaam = naamGeslachtsnaam;
    }

    public String getNaamVoorvoegsel() {
        return naamVoorvoegsel;
    }

    public void setNaamVoorvoegsel(final String naamVoorvoegsel) {
        this.naamVoorvoegsel = naamVoorvoegsel;
    }

    public String getNaamVoornamen() {
        return naamVoornamen;
    }

    public void setNaamVoornamen(final String naamVoornamen) {
        this.naamVoornamen = naamVoornamen;
    }

    public String getVerblijfplaatsGemeenteVanInschrijving() {
        return verblijfplaatsGemeenteVanInschrijving;
    }

    public void setVerblijfplaatsGemeenteVanInschrijving(final String verblijfplaatsGemeenteVanInschrijving) {
        this.verblijfplaatsGemeenteVanInschrijving = verblijfplaatsGemeenteVanInschrijving;
    }

    public String getVerblijfplaatsHuisletter() {
        return verblijfplaatsHuisletter;
    }

    public void setVerblijfplaatsHuisletter(final String verblijfplaatsHuisletter) {
        this.verblijfplaatsHuisletter = verblijfplaatsHuisletter;
    }

    public Integer getVerblijfplaatsHuisnummer() {
        return verblijfplaatsHuisnummer;
    }

    public void setVerblijfplaatsHuisnummer(final Integer verblijfplaatsHuisnummer) {
        this.verblijfplaatsHuisnummer = verblijfplaatsHuisnummer;
    }

    public String getVerblijfplaatsHuisnummertoevoeging() {
        return verblijfplaatsHuisnummertoevoeging;
    }

    public void setVerblijfplaatsHuisnummertoevoeging(final String verblijfplaatsHuisnummertoevoeging) {
        this.verblijfplaatsHuisnummertoevoeging = verblijfplaatsHuisnummertoevoeging;
    }

    public String getVerblijfplaatsNummeraanduidingIdentificatie() {
        return verblijfplaatsNummeraanduidingIdentificatie;
    }

    public void setVerblijfplaatsNummeraanduidingIdentificatie(final String verblijfplaatsNummeraanduidingIdentificatie) {
        this.verblijfplaatsNummeraanduidingIdentificatie = verblijfplaatsNummeraanduidingIdentificatie;
    }

    public String getVerblijfplaatsStraat() {
        return verblijfplaatsStraat;
    }

    public void setVerblijfplaatsStraat(final String verblijfplaatsStraat) {
        this.verblijfplaatsStraat = verblijfplaatsStraat;
    }

    public String getVerblijfplaatsPostcode() {
        return verblijfplaatsPostcode;
    }

    public void setVerblijfplaatsPostcode(final String verblijfplaatsPostcode) {
        this.verblijfplaatsPostcode = verblijfplaatsPostcode;
    }
}
