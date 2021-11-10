/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.ztc.model;

import java.net.URI;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

/**
 *
 */
public class Eigenschap {

    /**
     * URL-referentie naar dit object. Dit is de unieke identificatie en locatie van dit object.
     * - readOnly
     */
    private URI url;

    /**
     * De naam van de EIGENSCHAP.
     * - required
     * - maxlength 20
     */
    private String naam;

    /**
     * De beschrijving van de betekenis van deze EIGENSCHAP
     * - required
     * - maxlength 255
     */
    private String definitie;

    private EigenschapSpecificatie eigenschapSpecificatie;

    /**
     * Een toelichting op deze EIGENSCHAP en het belang hiervan voor zaken van dit ZAAKTYPE.
     * - maxLength 1000
     */
    private String toelichting;

    /**
     * URL-referentie naar het ZAAKTYPE van de ZAAKen waarvoor deze EIGENSCHAP van belang is.
     * - required
     */
    private URI zaaktype;

    public Eigenschap() {
    }

    /**
     * Cosntructor with required and readOnly attributes
     */
    @JsonbCreator
    public Eigenschap(@JsonbProperty("url") final URI url,
            @JsonbProperty("naam") final String naam,
            @JsonbProperty("definitie") final String definitie,
            @JsonbProperty("zaaktype") final URI zaaktype) {
        this.url = url;
        this.naam = naam;
        this.definitie = definitie;
        this.zaaktype = zaaktype;
    }

    public URI getUrl() {
        return url;
    }

    public void setUrl(final URI url) {
        this.url = url;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(final String naam) {
        this.naam = naam;
    }

    public String getDefinitie() {
        return definitie;
    }

    public void setDefinitie(final String definitie) {
        this.definitie = definitie;
    }

    public EigenschapSpecificatie getEigenschapSpecificatie() {
        return eigenschapSpecificatie;
    }

    public void setEigenschapSpecificatie(final EigenschapSpecificatie eigenschapSpecificatie) {
        this.eigenschapSpecificatie = eigenschapSpecificatie;
    }

    public String getToelichting() {
        return toelichting;
    }

    public void setToelichting(final String toelichting) {
        this.toelichting = toelichting;
    }

    public URI getZaaktype() {
        return zaaktype;
    }

    public void setZaaktype(final URI zaaktype) {
        this.zaaktype = zaaktype;
    }
}
