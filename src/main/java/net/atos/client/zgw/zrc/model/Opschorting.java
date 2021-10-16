/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

/**
 * Gegevens omtrent het tijdelijk opschorten van de behandeling van de ZAAK
 */
public class Opschorting {

    public static final int REDEN_MAX_LENGTH = 200;

    /**
     * Aanduiding of de behandeling van de ZAAK tijdelijk is opgeschort.
     */
    private final boolean indicatie;

    /**
     * Omschrijving van de reden voor het opschorten van de behandeling van de zaak.
     * <p>
     * maxLength: {@link Opschorting#REDEN_MAX_LENGTH}
     */
    private final String reden;

    /**
     * Constructor with required attributes for PUT and POST requests and GET response
     */
    @JsonbCreator
    public Opschorting(@JsonbProperty("indicatie") final boolean indicatie,
            @JsonbProperty("reden") final String reden) {
        this.indicatie = indicatie;
        this.reden = reden;
    }

    public boolean getIndicatie() {
        return indicatie;
    }

    public String getReden() {
        return reden;
    }
}
