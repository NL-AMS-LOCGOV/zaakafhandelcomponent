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
public class ZaakBesluit {

    /**
     * URL-referentie naar dit object.
     * Dit is de unieke identificatie en locatie van dit object.
     */
    private URI url;

    /**
     * Unieke resource identifier (UUID4)
     */
    private UUID uuid;

    /**
     * URL-referentie naar het BESLUIT (in de Zaken API), waar ook de relatieinformatie opgevraagd kan worden.
     */
    private URI besluit;


    /**
     * Constructor for PATCH request
     */
    public ZaakBesluit() {
    }

    /**
     * Constructor with required attributes for POST and PUT requests
     */
    public ZaakBesluit(final URI besluit) {
        this.besluit = besluit;
    }

    /**
     * Constructor with readOnly attributes for GET response
     */
    @JsonbCreator
    public ZaakBesluit(@JsonbProperty("url") final URI url,
            @JsonbProperty("uuid") final UUID uuid,
            @JsonbProperty("besluit") final URI besluit
    ) {
        this.url = url;
        this.uuid = uuid;
        this.besluit = besluit;
    }


    public URI getUrl() {
        return url;
    }

    public void setUrl(final URI url) {
        this.url = url;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(final UUID uuid) {
        this.uuid = uuid;
    }

    public URI getBesluit() {
        return besluit;
    }

    public void setBesluit(final URI besluit) {
        this.besluit = besluit;
    }
}
