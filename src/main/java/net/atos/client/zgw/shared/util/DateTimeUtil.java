/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 *
 */
public final class DateTimeUtil {

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssX";

    public static final String DATE_TIME_FORMAT_WITH_MILLISECONDS = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSX";

    public static ZonedDateTime convertToDateTime(final LocalDate date) {
        return date.atStartOfDay(ZoneId.systemDefault());
    }

    private DateTimeUtil() {
    }
}
