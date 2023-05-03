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
    ZAAK_BETROKKENE_ADVISEUR("zaak_betrokkene_adviseur"),
    ZAAK_BETROKKENE_BELANGHEBBENDE("zaak_betrokkene_belanghebbende"),
    ZAAK_BETROKKENE_BESLISSER("zaak_betrokkene_beslisser"),
    ZAAK_BETROKKENE_KLANTCONTACTER("zaak_betrokkene_klantcontacter"),
    ZAAK_BETROKKENE_ZAAKCOORDINATOR("zaak_betrokkene_zaakcoordinator"),
    ZAAK_BETROKKENE_MEDE_INITIATOR("zaak_betrokkene_mede_initiator"),
    ZAAK_BETROKKENEN("zaak_betrokkenen"),
    ZAAK_BAGOBJECTEN("zaak_bagObjecten"),

    TAAK_ZAAK_ID("taak_zaakId"),
    TAAK_TOELICHTING("taak_toelichting"),

    DOCUMENT_TITEL("informatieobject_titel"),
    DOCUMENT_BESCHRIJVING("informatieobject_beschrijving");

    private final String veld;

    ZoekVeld(final String veld) {
        this.veld = veld;
    }

    public String getVeld() {
        return veld;
    }
}
