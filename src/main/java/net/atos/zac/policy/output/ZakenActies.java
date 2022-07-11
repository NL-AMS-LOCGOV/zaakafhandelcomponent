/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.policy.output;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class ZakenActies {

    private final boolean verdelenEnVrijgeven;

    private final boolean toekennenAanMijzelf;

    @JsonbCreator
    public ZakenActies(@JsonbProperty("verdelen_en_vrijgeven") final boolean verdelenEnVrijgeven,
            @JsonbProperty("toekennen_aan_mijzelf") final boolean toekennenAanMijzelf) {
        this.verdelenEnVrijgeven = verdelenEnVrijgeven;
        this.toekennenAanMijzelf = toekennenAanMijzelf;
    }

    public boolean getVerdelenEnVrijgeven() {
        return verdelenEnVrijgeven;
    }

    public boolean getToekennenAanMijzelf() {
        return toekennenAanMijzelf;
    }
}
