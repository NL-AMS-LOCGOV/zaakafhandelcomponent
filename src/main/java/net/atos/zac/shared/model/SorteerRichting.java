/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.shared.model;

import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

public enum SorteerRichting {
    /** Oplopend */
    ASCENDING("asc"),

    /** Aflopend */
    DESCENDING("desc");

    private final String value;

    SorteerRichting(final String value) {
        this.value = value;
    }

    public static SorteerRichting fromValue(final String waarde) {
        if (StringUtils.isBlank(waarde)) {
            return SorteerRichting.DESCENDING;
        }
        return Stream.of(SorteerRichting.values())
                .filter(filter -> String.valueOf(filter.value).equals(waarde))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Onbekende waarde '%s'", waarde)));


    }
}
