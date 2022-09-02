/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.brc.model;

import java.net.URI;

import javax.ws.rs.QueryParam;

import net.atos.client.zgw.shared.model.AbstractListParameters;

/**
 * BesluitenListParameters
 */
public class BesluitenListParameters extends AbstractListParameters {

    /**
     * Identificatie van het besluit binnen de organisatie die het besluit heeft vastgesteld. Indien deze niet opgegeven is, dan wordt die gegenereerd.
     */
    @QueryParam("identificatie")
    private String identificatie;

    /**
     * Het RSIN van de niet-natuurlijk persoon zijnde de organisatie die het besluit heeft vastgesteld.
     */
    @QueryParam("verantwoordelijkeOrganisatie")
    private String verantwoordelijkeOrganisatie;

    /**
     * URL-referentie naar het BESLUITTYPE (in de Catalogi API).
     */
    @QueryParam("besluittype")
    private URI besluittype;

    /**
     * URL-referentie naar de ZAAK (in de Zaken API) waarvan dit besluit uitkomst is.
     */
    @QueryParam("zaak")
    private URI zaak;

    public BesluitenListParameters() {
    }

    public BesluitenListParameters(final URI zaak) {
        super();
        this.zaak = zaak;
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
}
