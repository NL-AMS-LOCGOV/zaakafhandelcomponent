/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.audit.model;

public class RESTWijziging {

    public String wijziging;

    public String oudeWaarde;

    public String nieuweWaarde;

    //public String doorGebruiker;
    public String omschrijving;

    public RESTWijziging(final String wijziging, final String oudeWaarde, final String nieuweWaarde) {
        this.wijziging = wijziging;
        this.oudeWaarde = oudeWaarde;
        this.nieuweWaarde = nieuweWaarde;
    }

    public RESTWijziging(final String wijziging) {
        this.wijziging = wijziging;
        this.oudeWaarde = "";
        this.nieuweWaarde = "";
    }

}
