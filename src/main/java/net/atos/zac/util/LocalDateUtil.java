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
        if (begin == null || end == null) {
            return false;
        }
        return (begin.isBefore(now) || begin.isEqual(now)) && (end.isAfter(now) || end.isEqual(now));
    }
}
