/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken.model;

public enum ZoekVeld {
    ALLE("text"),
    IDENTIFICATIE("identificatie"),
    ZAAK_OMSCHRIJVING("zaak_omschrijving"),
    ZAAK_TOELICHTING("zaak_toelichting");

    private final String veld;

    ZoekVeld(final String fieldList) {
        this.veld = fieldList;
    }

    public String getVeld() {
        return veld;
    }
}
