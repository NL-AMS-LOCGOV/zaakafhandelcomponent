/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.shared.model;

import org.apache.commons.lang3.StringUtils;

public enum SortDirection {
    /** Oplopend */
    ASCENDING("asc"),

    /** Aflopend */
    DESCENDING("desc");

    private final String value;

    SortDirection(final String value) {
        this.value = value;
    }

    public static SortDirection fromValue(final String value) {
        if (StringUtils.isBlank(value)) {
            return SortDirection.DESCENDING;
        }
        for (SortDirection sortDirection : SortDirection.values()) {
            if (String.valueOf(sortDirection.value).equals(value)) {
                return sortDirection;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
