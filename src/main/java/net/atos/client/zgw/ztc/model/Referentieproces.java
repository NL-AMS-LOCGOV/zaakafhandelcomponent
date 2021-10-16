/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.ztc.model;

import java.net.URI;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

/**
 * Het Referentieproces dat ten grondslag ligt aan dit ZAAKTYPE.
 */
public class Referentieproces {

    public static final int NAAM_MAX_LENGTH = 80;

    /**
     * De naam van het Referentieproces.
     * maxLength: {@link Referentieproces#NAAM_MAX_LENGTH}
     */
    private final String naam;

    /**
     * De URL naar de beschrijving van het Referentieproces
     */
    private URI link;

    /**
     * Constructor with required attributes for POST and PUT requests and GET response
     */
    @JsonbCreator
    public Referentieproces(@JsonbProperty("naam") final String naam) {
        this.naam = naam;
    }

    public String getNaam() {
        return naam;
    }

    public URI getLink() {
        return link;
    }

    public void setLink(final URI link) {
        this.link = link;
    }
}
