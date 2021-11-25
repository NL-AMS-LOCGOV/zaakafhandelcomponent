/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.audit.model;

public class RESTWijziging {

    public String veld;

    public String oudeWaarde;

    public String nieuweWaarde;

    //public String doorGebruiker;
    //public String reden;

    public RESTWijziging(final String veld, final String oudeWaarde, final String nieuweWaarde) {
        this.veld = veld;
        this.oudeWaarde = oudeWaarde;
        this.nieuweWaarde = nieuweWaarde;
    }

}
