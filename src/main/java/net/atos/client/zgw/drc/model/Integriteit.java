/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.drc.model;

import java.time.LocalDate;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

/**
 * Uitdrukking van mate van volledigheid en onbeschadigd zijn van digitaal bestand.
 */
public class Integriteit {

    public static final int WAARDE_MAX_LENGTH = 128;

    /**
     * Aanduiding van algoritme, gebruikt om de checksum te maken
     */
    private final Algoritme algoritme;

    /**
     * De waarde van de checksum.
     * maxLength: {@link Integriteit#WAARDE_MAX_LENGTH}
     */
    private final String waarde;

    /**
     * Datum waarop de checksum is gemaakt.
     */
    private final LocalDate datum;

    /**
     * Constructor with required attributes for POST and PUT requests and GET response
     */
    @JsonbCreator
    public Integriteit(@JsonbProperty("algoritme") final Algoritme algoritme,
            @JsonbProperty("waarde") final String waarde,
            @JsonbProperty("datum") final LocalDate datum) {
        this.algoritme = algoritme;
        this.waarde = waarde;
        this.datum = datum;
    }

    public Algoritme getAlgoritme() {
        return algoritme;
    }

    public String getWaarde() {
        return waarde;
    }

    public LocalDate getDatum() {
        return datum;
    }
}
