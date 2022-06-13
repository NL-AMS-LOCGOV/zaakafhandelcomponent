/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken.model;

import java.util.EnumSet;
import java.util.stream.Stream;

public enum FilterVeld {

    ZAAK_STATUS("zaak_statustypeOmschrijving"),
    ZAAK_ZAAKTYPE("zaak_zaaktypeOmschrijving"),
    ZAAK_ZAAKTYPE_UUID("zaak_zaaktypeUuid"),
    ZAAK_BEHANDELAAR("zaak_behandelaarNaam"),
    ZAAK_GROEP("zaak_groepNaam"),
    ZAAK_RESULTAAT("zaak_resultaattypeOmschrijving");

    public static final EnumSet<FilterVeld> ZAAK_FACETTEN = EnumSet.of(ZAAK_ZAAKTYPE, ZAAK_STATUS, ZAAK_BEHANDELAAR, ZAAK_GROEP, ZAAK_RESULTAAT);

    public static final EnumSet<FilterVeld> INFORMATIE_OBJECT_FACETTEN = EnumSet.noneOf(FilterVeld.class);

    private final String veld;

    FilterVeld(final String fieldList) {
        this.veld = fieldList;
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
}
