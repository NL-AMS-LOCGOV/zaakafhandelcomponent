/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken.model;

import java.util.stream.Stream;

public enum SorteerVeld {
    CREATED("created"),
    ZAAK_IDENTIFICATIE("zaak_identificatie"),
    ZAAK_ZAAKTYPE("zaak_zaaktypeOmschrijving"),
    ZAAK_BEHANDELAAR("zaak_behandelaarNaam"),
    ZAAK_GROEP("zaak_groepNaam"),
    ZAAK_REGISTRATIEDATUM("zaak_registratiedatum"),
    ZAAK_STARTDATUM("zaak_startdatum"),
    ZAAK_EINDDATUM_GEPLAND("zaak_einddatumGepland"),
    ZAAK_EINDDATUM("zaak_einddatum"),
    ZAAK_UITERLIJKE_EINDDATUM_AFDOENING("zaak_uiterlijkeEinddatumAfdoening"),
    ZAAK_COMMUNICATIEKANAAL("zaak_communicatiekanaal"),
    ZAAK_STATUS("zaak_statustypeOmschrijving"),
    ZAAK_RESULTAAT("zaak_resultaattypeOmschrijving"),
    ZAAK_AANTAL_OPENSTAANDE_TAKEN("zaak_aantalOpenstaandeTaken"),

    TAAK_NAAM("taak_naam"),
    TAAK_STATUS("taak_status"),
    TAAK_ZAAK_ID("taak_zaakId"),
    TAAK_ZAAKTYPE("taak_zaaktypeOmschrijving"),
    TAAK_BEHANDELAAR("taak_behandelaarNaam"),
    TAAK_GROEP("taak_groepNaam"),
    TAAK_CREATIEDATUM("taak_creatiedatum"),
    TAAK_TOEKENNINGSDATUM("taak_toekenningsdatum"),
    TAAK_STREEFDATUM("taak_streefdatum"),

    INFORMATIEOBJECT_TITEL("informatieobject_titel_sort"),
    INFORMATIEOBJECT_ZAAK_IDENTIFICATIE("informatieobject_zaakIdentificatie"),
    INFORMATIEOBJECT_CREATIEDATUM("informatieobject_creatiedatum"),
    INFORMATIEOBJECT_AUTEUR("informatieobject_auteur_sort"),
    INFORMATIEOBJECT_STATUS("informatieobject_status"),
    INFORMATIEOBJECT_REGISTRATIEDATUM("informatieobject_registratiedatum"),
    INFORMATIEOBJECT_DOCUMENTTYPE("informatieobject_documentType"),
    INFORMATIEOBJECT_ONTVANGSTDATUM("informatieobject_ontvangstdatum"),
    INFORMATIEOBJECT_VEZENDDATUM("informatieobject_vezenddatum");

    private final String veld;

    SorteerVeld(final String veld) {
        this.veld = veld;
    }

    public String getVeld() {
        return veld;
    }

    public static SorteerVeld fromValue(final String veld) {
        return Stream.of(SorteerVeld.values())
                .filter(filter -> String.valueOf(filter.veld).equals(veld))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Onbekend SorteerVeld '%s'", veld)));
    }
}
