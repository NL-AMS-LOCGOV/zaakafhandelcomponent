/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.taken.model;

import java.util.Arrays;

public enum TaakSortering {
    ID,
    TAAKNAAM,
    CREATIEDATUM,
    FATALEDATUM,
    BEHANDELAAR;

    public static TaakSortering fromValue(final String value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(taakSortering -> taakSortering.name().equalsIgnoreCase(value))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Onbekende TaakSortering met waarde: '%s'", value)));
    }
}
