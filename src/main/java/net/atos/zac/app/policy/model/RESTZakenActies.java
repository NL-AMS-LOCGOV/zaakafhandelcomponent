/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.policy.model;

import net.atos.zac.policy.output.ZakenActies;

public class RESTZakenActies {

    public boolean verdelenEnVrijgeven;

    public boolean toekennenAanMijzelf;

    public RESTZakenActies(final ZakenActies zakenActies) {
        verdelenEnVrijgeven = zakenActies.getVerdelenEnVrijgeven();
        toekennenAanMijzelf = zakenActies.getToekennenAanMijzelf();
    }
}
