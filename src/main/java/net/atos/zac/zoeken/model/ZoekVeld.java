/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken.model;

public enum ZoekVeld {
    ALLE("text"),

    ZAAK_IDENTIFICATIE("zaak_identificatie"),
    ZAAK_OMSCHRIJVING("zaak_omschrijving"),
    ZAAK_TOELICHTING("zaak_toelichting"),
    ZAAK_INITIATOR("zaak_initiatorIdentificatie"),

    TAAK_ZAAK_ID("taak_zaakId"),
    TAAK_TOELICHTING("taak_toelichting"),

    DOCUMENT_TITEL("informatieobject_titel"),
    DOCUMENT_BESCHRIJVING("informatieobject_beschrijving");

    private final String veld;

    ZoekVeld(final String fieldList) {
        this.veld = fieldList;
    }

    public String getVeld() {
        return veld;
    }
}
