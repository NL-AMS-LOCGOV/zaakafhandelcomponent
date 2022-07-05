/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.policy.output;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class AppActies {

    private final boolean aanmakenZaak;

    private final boolean beheren;

    @JsonbCreator
    public AppActies(@JsonbProperty("aanmaken_zaak") final boolean aanmakenZaak,
            @JsonbProperty("beheren") final boolean beheren) {
        this.aanmakenZaak = aanmakenZaak;
        this.beheren = beheren;
    }

    public boolean isAanmakenZaak() {
        return aanmakenZaak;
    }

    public boolean isBeheren() {
        return beheren;
    }
}
