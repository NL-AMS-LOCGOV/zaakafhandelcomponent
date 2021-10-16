/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import java.time.Period;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

/**
 * Gegevens omtrent het verlengen van de doorlooptijd van de behandeling
 */
public class Verlenging {

    public static final int REDEN_MAX_LENGTH = 200;

    /**
     * Omschrijving van de reden voor het verlengen van de behandeling
     * maxLength: {@link Verlenging#REDEN_MAX_LENGTH}
     */
    private final String reden;

    /**
     * Het aantal werkbare dagen waarmee de doorlooptijd van de behandeling van de ZAAK is verlengd (of verkort)
     * ten opzichte van de eerder gecommuniceerde doorlooptijd.
     */
    private final Period duur;

    /**
     * Constructor for required attributes for POST and PUT requests and GET response
     */
    @JsonbCreator
    public Verlenging(@JsonbProperty("reden") final String reden,
            @JsonbProperty("duur") final Period duur) {
        this.reden = reden;
        this.duur = duur;
    }

    public String getReden() {
        return reden;
    }

    public Period getDuur() {
        return duur;
    }
}
