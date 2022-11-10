/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken.model;

public enum DatumVeld {
    STARTDATUM("startdatum"),
    STREEFDATUM("streefdatum"),

    ZAAK_EINDDATUM("zaak_einddatum"),
    ZAAK_UITERLIJKE_EINDDATUM_AFDOENING("zaak_uiterlijkeEinddatumAfdoening"),
    ZAAK_REGISTRATIEDATUM("zaak_registratiedatum"),
    ZAAK_PUBLICATIEDATUM("zaak_publicatiedatum"),
    ZAAK_STATUS_DATUM_GEZET("zaak_statusDatumGezet"),

    TAAK_TOEKENNINGSDATUM("taak_toekenningsdatum"),

    INFORMATIEOBJECT_CREATIEDATUM("informatieobject_creatiedatum"),
    INFORMATIEOBJECT_ONTVANGSTDATUM("informatieobject_ontvangstdatum"),
    INFORMATIEOBJECT_VERZENDDATUM("informatieobject_verzenddatum");

    private final String veld;

    DatumVeld(final String fieldList) {
        this.veld = fieldList;
    }

    public String getVeld() {
        return veld;
    }
}
