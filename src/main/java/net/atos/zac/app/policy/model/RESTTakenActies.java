/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.policy.model;

import net.atos.zac.policy.output.TakenActies;

public class RESTTakenActies {

    public boolean verdelenEnVrijgeven;

    public boolean toekennenAanMijzelf;

    public RESTTakenActies(final TakenActies takenActies) {
        verdelenEnVrijgeven = takenActies.getVerdelenEnVrijgeven();
        toekennenAanMijzelf = takenActies.getToekennenAanMijzelf();
    }
}
