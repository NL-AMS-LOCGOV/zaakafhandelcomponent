/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.taken.model;

import java.util.Arrays;

public enum TaakSortering {
    TAAKNAAM,
    CREATIEDATUM,
    STREEFDATUM,
    BEHANDELAAR;

    public static TaakSortering fromValue(final String value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(taakSortering -> taakSortering.name().equals(value))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Onbekende TaakSortering met waarde: '%s'", value)));
    }
}
