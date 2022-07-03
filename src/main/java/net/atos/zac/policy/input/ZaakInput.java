/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.policy.input;

import net.atos.zac.authentication.LoggedInUser;

public class ZaakInput extends UserInput {

    private final ZaakData zaak;

    public ZaakInput(final LoggedInUser loggedInUser, final ZaakData zaak) {
        super(loggedInUser);
        this.zaak = zaak;
    }

    public ZaakData getZaak() {
        return zaak;
    }
}

