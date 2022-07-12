/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.policy.model;

import net.atos.zac.policy.output.AppActies;

public class RESTAppActies {

    public final boolean aanmakenZaak;

    public final boolean beheren;

    public final boolean zoeken;

    public final boolean zaken;

    public final boolean taken;

    public final boolean documenten;


    public RESTAppActies(final AppActies appActies) {
        this.aanmakenZaak = appActies.getAanmakenZaak();
        this.beheren = appActies.getBeheren();
        this.zoeken = appActies.getZoeken();
        this.zaken = appActies.getZaken();
        this.taken = appActies.getTaken();
        this.documenten = appActies.getDocumenten();
    }
}
