/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.policy.model;

import net.atos.zac.policy.output.AppActies;

public class RESTAppActies {

    public final boolean aanmakenZaak;

    public final boolean beheren;

    public RESTAppActies(final AppActies appActies) {
        this.aanmakenZaak = appActies.isAanmakenZaak();
        this.beheren = appActies.isBeheren();
    }
}
