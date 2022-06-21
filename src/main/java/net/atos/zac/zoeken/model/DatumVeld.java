/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken.model;

public enum DatumVeld {
    ZAAK_STARTDATUM("zaak_startdatum"),
    ZAAK_EINDDATUM("zaak_einddatum"),
    ZAAK_EINDDATUM_GEPLAND("zaak_einddatumGepland"),
    ZAAK_UITERLIJKE_EINDDATUM_AFDOENING("zaak_uiterlijkeEinddatumAfdoening"),
    ZAAK_REGISTRATIEDATUM("zaak_registratiedatum"),
    ZAAK_PUBLICATIEDATUM("zaak_publicatiedatum"),
    ZAAK_STATUS_DATUM_GEZET("zaak_statusDatumGezet"),

    TAAK_CREATIEDATUM("taak_creatiedatum"),
    TAAK_TOEKENNINGSDATUM("taak_toekenningsdatum"),
    TAAK_STREEFDATUM("taak_streefdatum"),

    INFORMATIEOBJECT_CREATIEDATUM("informatieobject_creatiedatum"),
    INFORMATIEOBJECT_REGISTRATIEDATUM("informatieobject_registratiedatum"),
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
