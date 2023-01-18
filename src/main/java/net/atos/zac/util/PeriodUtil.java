/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;

public final class PeriodUtil {

    private PeriodUtil() {
    }

    public static String format(Period period) {
        if (period == null) {
            return null;
        }
        if (period == Period.ZERO) {
            return "0 dagen";
        } else {
            StringBuilder buf = new StringBuilder();
            if (period.getYears() != 0) {
                if (period.getYears() == -1 || period.getYears() == 1) {
                    buf.append(period.getYears()).append(" jaar");
                } else {
                    buf.append(period.getYears()).append(" jaren");
                }
                if (period.getMonths() != 0 || period.getDays() != 0) {
                    buf.append(", ");
                }
            }

            if (period.getMonths() != 0) {
                if (period.getMonths() == -1 || period.getMonths() == 1) {
                    buf.append(period.getMonths()).append(" maand");
                } else {
                    buf.append(period.getMonths()).append(" maanden");
                }
                if (period.getDays() != 0) {
                    buf.append(", ");
                }
            }

            if (period.getDays() != 0) {
                if (period.getDays() == -1 || period.getDays() == 1) {
                    buf.append(period.getDays()).append(" dag");
                } else {
                    buf.append(period.getDays()).append(" dagen");
                }
            }
            return buf.toString();
        }
    }

    public static int aantalDagenVanafHeden(Period period) {
        if (period == null) {
            return 0;
        }
        LocalDateTime start = LocalDateTime.now();
        return Long.valueOf(start.until(start.plus(period), ChronoUnit.DAYS)).intValue();
    }
}
