/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import java.time.LocalDate;

import net.atos.client.zgw.ztc.model.Besluittype;

public final class LocalDateUtil {

    private LocalDateUtil() {
    }

    /**
     * Returns whether {@link LocalDate}.now() is between two dates.
     *
     * @param begin The lower-end of the date range
     * @param end   The higher-end of the date range
     * @return true if now <= begin and now < end, false otherwise. If any end of the date range is null it is not compared.
     */
    public static boolean dateNowIsBetween(LocalDate begin, LocalDate end) {
        final LocalDate now = LocalDate.now();
        return (begin == null || begin.isBefore(now) || begin.isEqual(now)) && (end == null || end.isAfter(now));
    }

    public static boolean dateNowIsBetween(Besluittype besluittype) {
        return dateNowIsBetween(besluittype.getBeginGeldigheid(), besluittype.getEindeGeldigheid());
    }
}
