/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.brc.model;

import java.net.URI;
import java.time.LocalDate;

import javax.json.bind.annotation.JsonbProperty;

/**
 * Besluit
 */
public class Besluit {

    /**
     * URL-referentie naar dit object. Dit is de unieke identificatie en locatie van dit object
     * - readOnly
     */
    private URI url;

    /**
     * Identificatie van het besluit binnen de organisatie die het besluit heeft vastgesteld. Indien deze niet opgegeven is, dan wordt die gegenereerd.
     * - readOnly
     * maxLength: 50
     */
    private String identificatie;

    /**
     * Het RSIN van de niet-natuurlijk persoon zijnde de organisatie die het besluit heeft vastgesteld.
     * - required
     */
    private String verantwoordelijkeOrganisatie;

    /**
     * URL-referentie naar het BESLUITTYPE (in de Catalogi API).
     * - required
     */
    private URI besluittype;

    /**
     * URL-referentie naar de ZAAK (in de Zaken API) waarvan dit besluit uitkomst is.
     */
    private URI zaak;

    /**
     * De beslisdatum (AWB) van het besluit.
     * - required
     */
    private LocalDate datum;

    /**
     * Toelichting bij het besluit.
     */
    private String toelichting;

    /**
     * Een orgaan van een rechtspersoon krachtens publiekrecht ingesteld of een persoon of college,
     * met enig openbaar gezag bekleed onder wiens verantwoordelijkheid het besluit vastgesteld is.
     * <p>
     * maxLength: 50
     */
    private String bestuursorgaan;

    /**
     * Ingangsdatum van de werkingsperiode van het besluit.
     * - required
     */
    private LocalDate ingangsdatum;

    /**
     * Datum waarop de werkingsperiode van het besluit eindigt.
     */
    @JsonbProperty(nillable = true)
    private LocalDate vervaldatum;

    /**
     * De omschrijving die aangeeft op grond waarvan het besluit is of komt te vervallen.
     */
    @JsonbProperty(nillable = true)
    private Vervalreden vervalreden;

    /**
     * Vervalreden weergave
     */
    private String vervalredenWeergave;

    /**
     * Datum waarop het besluit gepubliceerd wordt.
     */
    private LocalDate publicatiedatum;

    /**
     * Datum waarop het besluit verzonden is.
     */
    private LocalDate verzenddatum;

    /**
     * De datum tot wanneer verweer tegen het besluit mogelijk is.
     */
    private LocalDate uiterlijkeReactiedatum;


    public Besluit() {
    }

    public URI getUrl() {
        return url;
    }

    public void setUrl(final URI url) {
        this.url = url;
    }

    public String getIdentificatie() {
        return identificatie;
    }

    public void setIdentificatie(final String identificatie) {
        this.identificatie = identificatie;
    }

    public String getVerantwoordelijkeOrganisatie() {
        return verantwoordelijkeOrganisatie;
    }

    public void setVerantwoordelijkeOrganisatie(final String verantwoordelijkeOrganisatie) {
        this.verantwoordelijkeOrganisatie = verantwoordelijkeOrganisatie;
    }

    public URI getBesluittype() {
        return besluittype;
    }

    public void setBesluittype(final URI besluittype) {
        this.besluittype = besluittype;
    }

    public URI getZaak() {
        return zaak;
    }

    public void setZaak(final URI zaak) {
        this.zaak = zaak;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public void setDatum(final LocalDate datum) {
        this.datum = datum;
    }

    public String getToelichting() {
        return toelichting;
    }

    public void setToelichting(final String toelichting) {
        this.toelichting = toelichting;
    }

    public String getBestuursorgaan() {
        return bestuursorgaan;
    }

    public void setBestuursorgaan(final String bestuursorgaan) {
        this.bestuursorgaan = bestuursorgaan;
    }

    public LocalDate getIngangsdatum() {
        return ingangsdatum;
    }

    public void setIngangsdatum(final LocalDate ingangsdatum) {
        this.ingangsdatum = ingangsdatum;
    }

    public LocalDate getVervaldatum() {
        return vervaldatum;
    }

    public void setVervaldatum(final LocalDate vervaldatum) {
        this.vervaldatum = vervaldatum;
    }

    public Vervalreden getVervalreden() {
        return vervalreden;
    }

    public void setVervalreden(final Vervalreden vervalreden) {
        this.vervalreden = vervalreden;
    }

    public String getVervalredenWeergave() {
        return vervalredenWeergave;
    }

    public void setVervalredenWeergave(final String vervalredenWeergave) {
        this.vervalredenWeergave = vervalredenWeergave;
    }

    public LocalDate getPublicatiedatum() {
        return publicatiedatum;
    }

    public void setPublicatiedatum(final LocalDate publicatiedatum) {
        this.publicatiedatum = publicatiedatum;
    }

    public LocalDate getVerzenddatum() {
        return verzenddatum;
    }

    public void setVerzenddatum(final LocalDate verzenddatum) {
        this.verzenddatum = verzenddatum;
    }

    public LocalDate getUiterlijkeReactiedatum() {
        return uiterlijkeReactiedatum;
    }

    public void setUiterlijkeReactiedatum(final LocalDate uiterlijkeReactiedatum) {
        this.uiterlijkeReactiedatum = uiterlijkeReactiedatum;
    }
}
