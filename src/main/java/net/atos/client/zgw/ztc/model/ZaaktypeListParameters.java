/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.ztc.model;

import java.net.URI;

import javax.ws.rs.QueryParam;

/**
 *
 */
public class ZaaktypeListParameters extends AbstractZTCListParameters {

    /**
     * URL-referentie naar de CATALOGUS waartoe dit ZAAKTYPE behoort.
     */
    @QueryParam("catalogus")
    private URI catalogus;

    /**
     * Unieke identificatie van het ZAAKTYPE binnen de CATALOGUS waarin het ZAAKTYPE voorkomt.
     */
    @QueryParam("identificatie")
    private String identificatie;

    /**
     * Multiple values may be separated by commas.
     */
    @QueryParam("trefwoorden")
    private String trefwoorden;


    public ZaaktypeListParameters() {
    }

    public ZaaktypeListParameters(final URI catalogus) {
        this.catalogus = catalogus;
    }

    public URI getCatalogus() {
        return catalogus;
    }

    public void setCatalogus(final URI catalogus) {
        this.catalogus = catalogus;
    }

    public String getIdentificatie() {
        return identificatie;
    }

    public void setIdentificatie(final String identificatie) {
        this.identificatie = identificatie;
    }

    public String getTrefwoorden() {
        return trefwoorden;
    }

    public void setTrefwoorden(final String trefwoorden) {
        this.trefwoorden = trefwoorden;
    }
}
