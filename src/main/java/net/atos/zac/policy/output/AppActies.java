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

    private final boolean zoeken;

    private final boolean zaken;

    private final boolean taken;

    private final boolean documenten;

    @JsonbCreator
    public AppActies(@JsonbProperty("aanmaken_zaak") final boolean aanmakenZaak,
            @JsonbProperty("beheren") final boolean beheren,
            @JsonbProperty("zoeken") final boolean zoeken,
            @JsonbProperty("zaken") final boolean zaken,
            @JsonbProperty("taken") final boolean taken,
            @JsonbProperty("documenten") final boolean documenten) {
        this.aanmakenZaak = aanmakenZaak;
        this.zoeken = zoeken;
        this.zaken = zaken;
        this.taken = taken;
        this.documenten = documenten;
        this.beheren = beheren;
    }

    public boolean getAanmakenZaak() {
        return aanmakenZaak;
    }

    public boolean getBeheren() {
        return beheren;
    }

    public boolean getZoeken() {
        return zoeken;
    }

    public boolean getZaken() {
        return zaken;
    }

    public boolean getTaken() {
        return taken;
    }

    public boolean getDocumenten() {
        return documenten;
    }
}
