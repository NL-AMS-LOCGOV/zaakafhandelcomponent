/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import java.net.URI;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

/**
 * Een relevante andere zaak.
 */
public class RelevanteZaak {

    /**
     * URL-referentie naar de ZAAK.
     */
    private final URI url;

    /**
     * 'Benamingen van de aard van de relaties van andere zaken tot (onderhanden) zaken.
     */
    private final AardRelatie aardRelatie;

    /**
     * Constructor with required attributes for POST and PUT requests and GET response
     */
    @JsonbCreator
    public RelevanteZaak(@JsonbProperty("url") final URI url,
            @JsonbProperty("aardRelatie") final AardRelatie aardRelatie) {
        this.url = url;
        this.aardRelatie = aardRelatie;
    }

    public URI getUrl() {
        return url;
    }

    public AardRelatie getAardRelatie() {
        return aardRelatie;
    }

    public boolean is(final URI url, final AardRelatie aardRelatie) {
        return this.aardRelatie == aardRelatie && this.url.equals(url);
    }
}
