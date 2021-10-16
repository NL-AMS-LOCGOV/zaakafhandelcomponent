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
public class Roltype {

    public static final int OMSCHRIJVING_MAX_LENGTH = 80;

    /**
     * URL-referentie naar dit object.
     * Dit is de unieke identificatie en locatie van dit object.
     */
    private URI url;

    /**
     * URL-referentie naar het ZAAKTYPE waar deze ROLTYPEn betrokken kunnen zijn.
     */
    private URI zaaktype;

    /**
     * Omschrijving van de aard van de ROL.
     * maxLength: {@link Roltype#OMSCHRIJVING_MAX_LENGTH}
     */
    private String omschrijving;

    /**
     * Algemeen gehanteerde omschrijving van de aard van de ROL.
     */
    private AardVanRol omschrijvingGeneriek;

    /**
     * Constructor for PATCH request
     */
    public Roltype() {
    }

    /**
     * Constructror with required attributes for POST and PUT request
     */
    public Roltype(final URI zaaktype, final String omschrijving, final AardVanRol omschrijvingGeneriek) {
        this.zaaktype = zaaktype;
        this.omschrijving = omschrijving;
        this.omschrijvingGeneriek = omschrijvingGeneriek;
    }

    /**
     * Constructor with readOnly attributes for GET response
     */
    @JsonbCreator
    public Roltype(@JsonbProperty("url") final URI url) {
        this.url = url;
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

    public AardVanRol getOmschrijvingGeneriek() {
        return omschrijvingGeneriek;
    }

    public void setOmschrijvingGeneriek(final AardVanRol omschrijvingGeneriek) {
        this.omschrijvingGeneriek = omschrijvingGeneriek;
    }
}
