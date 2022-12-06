/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import java.time.LocalDate;

public final class LocalDateUtil {

    private LocalDateUtil() {
    }

    public static boolean dateNowIsBetweenInclusive(LocalDate begin, LocalDate end) {
        final LocalDate now = LocalDate.now();
        return (begin == null || begin.isBefore(now) || begin.isEqual(now)) && (end == null || end.isAfter(now) || end.isEqual(now));
    }
}
