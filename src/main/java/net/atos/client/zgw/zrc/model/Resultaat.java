/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import java.net.URI;
import java.util.UUID;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

/**
 *
 */
public class Resultaat {

    public static final int TOELICHTING_MAX_LENGTH = 1000;

    /**
     * URL-referentie naar dit object. Dit is de unieke identificatie en locatie van dit object.
     */
    private URI url;

    /**
     * Unieke resource identifier (UUID4)
     */
    private UUID uuid;

    /**
     * URL-referentie naar de ZAAK.
     */
    private URI zaak;

    /**
     * URL-referentie naar het RESULTAATTYPE (in de Catalogi API).
     */
    private URI resultaattype;

    /**
     * Een toelichting op wat het resultaat van de zaak inhoudt.
     * maxLength: {@link Resultaat#TOELICHTING_MAX_LENGTH}
     */
    private String toelichting;

    /**
     * Constructor for PATCH request
     */
    public Resultaat() {
    }

    /**
     * Constructor with required attributes for POST and PUT requests
     */
    public Resultaat(final URI zaak, final URI resultaattype) {
        this.zaak = zaak;
        this.resultaattype = resultaattype;
    }

    /**
     * Constructor for readOnly attributes from GET response
     */
    @JsonbCreator
    public Resultaat(@JsonbProperty("url") final URI url,
            @JsonbProperty("uuid") final UUID uuid) {
        this.url = url;
        this.uuid = uuid;
    }

    public URI getUrl() {
        return url;
    }

    public UUID getUuid() {
        return uuid;
    }

    public URI getZaak() {
        return zaak;
    }

    public void setZaak(final URI zaak) {
        this.zaak = zaak;
    }

    public URI getResultaattype() {
        return resultaattype;
    }

    public void setResultaattype(final URI resultaattype) {
        this.resultaattype = resultaattype;
    }

    public String getToelichting() {
        return toelichting;
    }

    public void setToelichting(final String toelichting) {
        this.toelichting = toelichting;
    }
}
