/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.ztc.model;

import java.net.URI;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

/**
 * De ZAAKTYPEn van zaken die relevant zijn voor zaken van dit ZAAKTYPE.
 */
public class ZaakTypenRelatie {

    public static final int TOELICHTING_MAX_LENGTH = 255;

    /**
     * URL referentie naar het gerelateerde zaaktype, mogelijks in een extern ZTC.
     */
    private final URI zaaktype;

    /**
     * Omschrijving van de aard van de relatie van zaken van het ZAAKTYPE tot zaken van het andere ZAAKTYPE
     */
    private final AardRelatie aardRelatie;

    /**
     * Een toelichting op de aard van de relatie tussen beide ZAAKTYPEN.
     * maxLength: {@link ZaakTypenRelatie#TOELICHTING_MAX_LENGTH}
     */
    private String toelichting;

    /**
     * Constructor with required attributes for Post and Put request and for GET response
     */
    @JsonbCreator
    public ZaakTypenRelatie(@JsonbProperty("zaaktype") final URI zaaktype,
            @JsonbProperty("aardRelatie") final AardRelatie aardRelatie) {
        this.zaaktype = zaaktype;
        this.aardRelatie = aardRelatie;
    }

    public URI getZaaktype() {
        return zaaktype;
    }

    public AardRelatie getAardRelatie() {
        return aardRelatie;
    }

    public String getToelichting() {
        return toelichting;
    }

    public void setToelichting(final String toelichting) {
        this.toelichting = toelichting;
    }
}
