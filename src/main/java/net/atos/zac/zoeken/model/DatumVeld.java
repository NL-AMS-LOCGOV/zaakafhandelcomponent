/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken.model;

public enum DatumVeld {
    STARTDATUM("startdatum"), // TODO #1438, unused Solr veld opruimen?
    STREEFDATUM("streefdatum"), // TODO #1438, unused Solr veld opruimen?

    ZAAK_STARTDATUM("zaak_startdatum"),
    ZAAK_EINDDATUM("zaak_einddatum"),
    ZAAK_STREEFDATUM("zaak_einddatumGepland"),
    ZAAK_FATALE_DATUM("zaak_uiterlijkeEinddatumAfdoening"),
    ZAAK_REGISTRATIEDATUM("zaak_registratiedatum"),
    ZAAK_PUBLICATIEDATUM("zaak_publicatiedatum"),
    ZAAK_STATUS_DATUM_GEZET("zaak_statusDatumGezet"),

    TAAK_CREATIEDATUM("taak_creatiedatum"),
    TAAK_TOEKENNINGSDATUM("taak_toekenningsdatum"),
    TAAK_FATALEDATUM("taak_streefdatum"), // TODO #1438, Solr veld renamen

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
