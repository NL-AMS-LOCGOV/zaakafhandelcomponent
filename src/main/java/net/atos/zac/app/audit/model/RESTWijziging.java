/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.audit.model;

import org.apache.commons.lang3.StringUtils;

public class RESTWijziging {

    public String wijziging;

    public String oudeWaarde;

    public String nieuweWaarde;

    //public String doorGebruiker;
    public String omschrijving;

    public RESTWijziging(final String wijziging, final String oudeWaarde, final String nieuweWaarde) {
        this.wijziging = wijziging;
        this.oudeWaarde = oudeWaarde != null ? oudeWaarde : StringUtils.EMPTY;
        this.nieuweWaarde = nieuweWaarde != null ? nieuweWaarde : StringUtils.EMPTY;
    }

    public RESTWijziging(final String wijziging) {
        this(wijziging, null, null);
    }

    public RESTWijziging() {
        this("Onbekend veld gewijzigd");
    }
}
