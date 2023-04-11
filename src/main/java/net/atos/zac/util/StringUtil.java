/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import static java.util.stream.Collectors.joining;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

public final class StringUtil {

    public static final String ONBEKEND = "<onbekend>";

    public static final String NON_BREAKING_SPACE = String.valueOf('\u00A0');

    private StringUtil() {
    }

    public static String joinNonBlankWith(final String delimiter, final String... parts) {
        return Arrays.stream(parts)
                .filter(StringUtils::isNotBlank)
                .collect(joining(delimiter));
    }

    public static String joinNonBlank(final String... parts) {
        return Arrays.stream(parts)
                .filter(StringUtils::isNotBlank)
                .collect(joining());
    }
}
