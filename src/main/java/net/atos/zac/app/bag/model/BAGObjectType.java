/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.bag.model;

public enum BAGObjectType {
    ADRES("adressen"),
    WOONPLAATS("woonplaats"),
    PAND("panden"),
    OPENBARE_RUIMTE("openbareRuimte"),
    NUMMERAANDUIDING("nummeraanduiding"),
    ADRESSEERBAAR_OBJECT("adreseerbaarObject");

    public final String expand;

    BAGObjectType(final String expand) {
        this.expand = expand;
    }

    public String getExpand() {
        return expand;
    }
}
