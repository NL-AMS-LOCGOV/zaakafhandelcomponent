/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.ztc.model;

import java.net.URI;
import java.time.Period;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

import net.atos.client.zgw.shared.model.Archiefnominatie;

/**
 *
 */
public class Resultaattype {

    public static final int OMSCHRIJVING_MAX_LENGTH = 20;

    public static final int RESULTAATTYPEOMSCHRIJVING_MAX_LENGTH = 1000;

    /**
     * URL-referentie naar dit object.
     * Dit is de unieke identificatie en locatie van dit object.
     */
    private URI url;

    /**
     * URL-referentie naar het ZAAKTYPE van ZAAKen waarin resultaten van dit RESULTAATTYPE bereikt kunnen worden.
     */
    private URI zaaktype;

    /**
     * Omschrijving van de aard van resultaten van het RESULTAATTYPE.
     * maxLength: {@link Resultaattype#OMSCHRIJVING_MAX_LENGTH}
     */
    private String omschrijving;

    /**
     * Algemeen gehanteerde omschrijving van de aard van resultaten van het RESULTAATTYPE.
     * Dit moet een URL-referentie zijn naar de referenlijst van generieke resultaattypeomschrijvingen.
     * Im ImZTC heet dit 'omschrijving generiek'
     * maxLength: {@link Resultaattype#RESULTAATTYPEOMSCHRIJVING_MAX_LENGTH}
     */
    private String resultaattypeomschrijving;

    /**
     * Waarde van de omschrijving-generiek referentie (attribuut `omschrijving`)
     */
    private String omschrijvingGeneriek;

    /**
     * URL-referentie naar de, voor het archiefregime bij het RESULTAATTYPE relevante,
     * categorie in de Selectielijst Archiefbescheiden (RESULTAAT in de Selectielijst API) van de voor het ZAAKTYPE verantwoordelijke overheidsorganisatie.
     */
    private URI selectielijstklasse;

    /**
     * Een toelichting op dit RESULTAATTYPE en het belang hiervan voor ZAAKen waarin een resultaat van dit RESULTAATTYPE wordt geselecteerd.
     */
    private String toelichting;

    /**
     * anduiding die aangeeft of ZAAKen met een resultaat van dit RESULTAATTYPE blijvend moeten worden bewaard of (op termijn) moeten worden vernietigd.
     * Indien niet expliciet opgegeven wordt dit gevuld vanuit de selectielijst.
     */
    private Archiefnominatie archiefnominatie;

    /**
     * De termijn, na het vervallen van het bedrjfsvoeringsbelang, waarna het zaakdossier (de ZAAK met alle bijbehorende INFORMATIEOBJECTen) van een ZAAK
     * met een resultaat van dit RESULTAATTYPE vernietigd of overgebracht (naar een archiefbewaarplaats) moet worden.
     * Voor te vernietigen dossiers betreft het de in die Selectielijst genoemde bewaartermjn.
     * Voor blijvend te bewaren zaakdossiers betreft het de termijn vanaf afronding van de zaak tot overbrenging (de procestermijn is dan nihil).
     */
    private Period archiefactietermijn;

    private BrondatumArchiefprocedure brondatumArchiefprocedure;

    /**
     * Constructor for PATCH request
     */
    public Resultaattype() {
    }

    /**
     * Constructor with required attributes for POST and PUT request
     */
    public Resultaattype(final URI zaaktype, final String omschrijving, final String resultaattypeomschrijving, final URI selectielijstklasse) {
        this.zaaktype = zaaktype;
        this.omschrijving = omschrijving;
        this.resultaattypeomschrijving = resultaattypeomschrijving;
        this.selectielijstklasse = selectielijstklasse;
    }

    /**
     * Constructor with readOnly attributes for GET response
     */
    @JsonbCreator
    public Resultaattype(@JsonbProperty("url") final URI url,
            @JsonbProperty("omschrijvingGeneriek") final String omschrijvingGeneriek) {
        this.url = url;
        this.omschrijvingGeneriek = omschrijvingGeneriek;
    }

    public URI getUrl() {
        return url;
    }

    public URI getZaaktype() {
        return zaaktype;
    }

    public void setZaaktype(final URI zaaktype) {
        this.zaaktype = zaaktype;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

    public void setOmschrijving(final String omschrijving) {
        this.omschrijving = omschrijving;
    }

    public String getResultaattypeomschrijving() {
        return resultaattypeomschrijving;
    }

    public void setResultaattypeomschrijving(final String resultaattypeomschrijving) {
        this.resultaattypeomschrijving = resultaattypeomschrijving;
    }

    public String getOmschrijvingGeneriek() {
        return omschrijvingGeneriek;
    }

    public URI getSelectielijstklasse() {
        return selectielijstklasse;
    }

    public void setSelectielijstklasse(final URI selectielijstklasse) {
        this.selectielijstklasse = selectielijstklasse;
    }

    public String getToelichting() {
        return toelichting;
    }

    public void setToelichting(final String toelichting) {
        this.toelichting = toelichting;
    }

    public Archiefnominatie getArchiefnominatie() {
        return archiefnominatie;
    }

    public void setArchiefnominatie(final Archiefnominatie archiefnominatie) {
        this.archiefnominatie = archiefnominatie;
    }

    public Period getArchiefactietermijn() {
        return archiefactietermijn;
    }

    public void setArchiefactietermijn(final Period archiefactietermijn) {
        this.archiefactietermijn = archiefactietermijn;
    }

    public BrondatumArchiefprocedure getBrondatumArchiefprocedure() {
        return brondatumArchiefprocedure;
    }

    public void setBrondatumArchiefprocedure(final BrondatumArchiefprocedure brondatumArchiefprocedure) {
        this.brondatumArchiefprocedure = brondatumArchiefprocedure;
    }
}
