/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.policy.output;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class OverigeRechten {

    private final boolean startenZaak;

    private final boolean beheren;

    private final boolean zoeken;

    @JsonbCreator
    public OverigeRechten(
            @JsonbProperty("starten_zaak") final boolean startenZaak,
            @JsonbProperty("beheren") final boolean beheren,
            @JsonbProperty("zoeken") final boolean zoeken) {
        this.startenZaak = startenZaak;
        this.beheren = beheren;
        this.zoeken = zoeken;
    }

    public boolean getStartenZaak() {
        return startenZaak;
    }

    public boolean getBeheren() {
        return beheren;
    }

    public boolean getZoeken() {
        return zoeken;
    }
}
