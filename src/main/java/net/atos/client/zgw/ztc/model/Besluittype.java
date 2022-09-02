/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.ztc.model;

import java.net.URI;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class Besluittype {

    public static final int OMSCHRIJVING_MAX_LENGTH = 80;

    public static final int BESLUITCATEGORIE_MAX_LENGTH = 40;

    /**
     * URL-referentie naar dit object.
     * Dit is de unieke identificatie en locatie van dit object.
     */
    private URI url;

    /**
     * URL-referentie naar de CATALOGUS waartoe dit BESLUITTYPE behoort.
     * - required
     */
    private URI catalogus;

    /**
     * ZAAKTYPE met ZAAKen die relevant kunnen zijn voor dit BESLUITTYPE
     * - required
     */
    private List<URI> zaaktypen;

    /**
     * Omschrijving van de aard van BESLUITen van het BESLUITTYPE.
     * maxLength: {@link Besluittype#OMSCHRIJVING_MAX_LENGTH}
     */
    private String omschrijving;

    /**
     * Algemeen gehanteerde omschrijving van de aard van BESLUITen van het BESLUITTYPE
     * maxLength: {@link Besluittype#OMSCHRIJVING_MAX_LENGTH}
     */
    private String omschrijvingGeneriek;

    /**
     * Typering van de aard van BESLUITen van het BESLUITTYPE.
     * maxLength: {@link Besluittype#BESLUITCATEGORIE_MAX_LENGTH}
     */
    private String besluitcategorie;

    /**
     * De duur (typisch een aantal dagen), gerekend vanaf de verzend- of publicatiedatum,
     * waarbinnen verweer tegen een besluit van het besluittype mogelijk is.
     */
    private Period reactietermijn;

    /**
     * Aanduiding of BESLUITen van dit BESLUITTYPE gepubliceerd moeten worden.
     * - required
     */
    private boolean publicatieIndicatie;

    /**
     * De generieke tekst van de publicatie van BESLUITen van dit BESLUITTYPE
     */
    private String publicatietekst;

    /**
     * De duur (typisch een aantal dagen), gerekend vanaf de verzend- of publicatiedatum,
     * dat BESLUITen van dit BESLUITTYPE gepubliceerd moeten blijven.
     */
    private Period publicatietermijn;

    /**
     * Een eventuele toelichting op dit BESLUITTYPE.
     */
    private String toelichting;

    /**
     * URL-referenties naar het INFORMATIEOBJECTTYPE van informatieobjecten waarin besluiten van dit BESLUITTYPE worden vastgelegd.
     * - required
     */
    private List<URI> informatieobjecttypen;

    /**
     * De datum waarop het besluittype is ontstaan.
     * - required
     */
    private LocalDate beginGeldigheid;

    /**
     * De datum waarop het besluittype is opgeheven.
     */
    private LocalDate eindeGeldigheid;

    /**
     * Geeft aan of het object een concept betreft. Concepten zijn niet-definitieve versies en zouden niet gebruikt moeten worden buiten deze API.
     */
    private boolean concept;


    public Besluittype() {
    }

    public URI getUrl() {
        return url;
    }

    public void setUrl(final URI url) {
        this.url = url;
    }

    public URI getCatalogus() {
        return catalogus;
    }

    public void setCatalogus(final URI catalogus) {
        this.catalogus = catalogus;
    }

    public List<URI> getZaaktypen() {
        return zaaktypen;
    }

    public void setZaaktypen(final List<URI> zaaktypen) {
        this.zaaktypen = zaaktypen;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

    public void setOmschrijving(final String omschrijving) {
        this.omschrijving = omschrijving;
    }

    public String getOmschrijvingGeneriek() {
        return omschrijvingGeneriek;
    }

    public void setOmschrijvingGeneriek(final String omschrijvingGeneriek) {
        this.omschrijvingGeneriek = omschrijvingGeneriek;
    }

    public String getBesluitcategorie() {
        return besluitcategorie;
    }

    public void setBesluitcategorie(final String besluitcategorie) {
        this.besluitcategorie = besluitcategorie;
    }

    public Period getReactietermijn() {
        return reactietermijn;
    }

    public void setReactietermijn(final Period reactietermijn) {
        this.reactietermijn = reactietermijn;
    }

    public boolean isPublicatieIndicatie() {
        return publicatieIndicatie;
    }

    public void setPublicatieIndicatie(final boolean publicatieIndicatie) {
        this.publicatieIndicatie = publicatieIndicatie;
    }

    public String getPublicatietekst() {
        return publicatietekst;
    }

    public void setPublicatietekst(final String publicatietekst) {
        this.publicatietekst = publicatietekst;
    }

    public Period getPublicatietermijn() {
        return publicatietermijn;
    }

    public void setPublicatietermijn(final Period publicatietermijn) {
        this.publicatietermijn = publicatietermijn;
    }

    public String getToelichting() {
        return toelichting;
    }

    public void setToelichting(final String toelichting) {
        this.toelichting = toelichting;
    }

    public List<URI> getInformatieobjecttypen() {
        return informatieobjecttypen;
    }

    public void setInformatieobjecttypen(final List<URI> informatieobjecttypen) {
        this.informatieobjecttypen = informatieobjecttypen;
    }

    public LocalDate getBeginGeldigheid() {
        return beginGeldigheid;
    }

    public void setBeginGeldigheid(final LocalDate beginGeldigheid) {
        this.beginGeldigheid = beginGeldigheid;
    }

    public LocalDate getEindeGeldigheid() {
        return eindeGeldigheid;
    }

    public void setEindeGeldigheid(final LocalDate eindeGeldigheid) {
        this.eindeGeldigheid = eindeGeldigheid;
    }

    public boolean isConcept() {
        return concept;
    }

    public void setConcept(final boolean concept) {
        this.concept = concept;
    }

}
