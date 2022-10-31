/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken.model;

import java.util.EnumSet;
import java.util.stream.Stream;

public enum FilterVeld {

    TYPE("type"),
    ZAAKTYPE("zaaktypeOmschrijving"),
    BEHANDELAAR("behandelaarNaam"),
    GROEP("groepNaam"),
    TOEGEKEND("isToegekend"),

    ZAAK_STATUS("zaak_statustypeOmschrijving"),
    ZAAK_ZAAKTYPE("zaak_zaaktypeOmschrijving"),
    ZAAK_ZAAKTYPE_UUID("zaak_zaaktypeUuid"),
    ZAAK_BEHANDELAAR("zaak_behandelaarNaam"),
    ZAAK_GROEP("zaak_groepNaam"),
    ZAAK_RESULTAAT("zaak_resultaattypeOmschrijving"),

    TAAK_NAAM("taak_naam"),
    TAAK_STATUS("taak_status"),
    TAAK_ZAAKTYPE("taak_zaaktypeOmschrijving"),
    TAAK_BEHANDELAAR("taak_behandelaarNaam"),
    TAAK_GROEP("taak_groepNaam");

    private final String veld;

    FilterVeld(final String veld) {
        this.veld = veld;
    }

    public String getVeld() {
        return veld;
    }

    public static FilterVeld fromValue(final String veld) {
        return Stream.of(FilterVeld.values())
                .filter(filter -> String.valueOf(filter.veld).equals(veld))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Onbekend Filterveld '%s'", veld)));
    }

    public static EnumSet<FilterVeld> getFacetten() {
        return EnumSet.of(TYPE, ZAAKTYPE, BEHANDELAAR, GROEP, ZAAK_STATUS, ZAAK_RESULTAAT, TAAK_NAAM, TAAK_STATUS);
    }

    public static EnumSet<FilterVeld> getTaakFacetten() {
        return EnumSet.of(TAAK_NAAM, TAAK_STATUS, GROEP, BEHANDELAAR, ZAAKTYPE);
    }

    public static EnumSet<FilterVeld> getZaakFacetten() {
        return EnumSet.of(ZAAKTYPE, ZAAK_STATUS, BEHANDELAAR, GROEP, ZAAK_RESULTAAT);
    }

}
