/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

/**
 *
 */
public final class DateTimeConverterUtil {

    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();

    private DateTimeConverterUtil() {
    }

    public static LocalDate convertToLocalDate(final Date date) {
        return date != null ? LocalDate.ofInstant(date.toInstant(), DEFAULT_ZONE_ID) : null;
    }

    public static ZonedDateTime convertToZonedDateTime(final Date dateTime) {
        return dateTime != null ? ZonedDateTime.ofInstant(dateTime.toInstant(), DEFAULT_ZONE_ID) : null;
    }

    public static Date convertToDate(final LocalDate localDate) {
        return localDate != null ? Date.from(localDate.atStartOfDay().atZone(DEFAULT_ZONE_ID).toInstant()) : null;
    }

    public static Date convertToDate(final ZonedDateTime zonedDateTime) {
        return zonedDateTime != null ? Date.from(zonedDateTime.withZoneSameInstant(DEFAULT_ZONE_ID).toInstant()) : null;
    }

    public static Date convertToDate(final String isoString) {
        return StringUtils.isNotBlank(isoString) ? convertToDate(ZonedDateTime.parse(isoString)) : null;
    }

    public static LocalDateTime convertToLocalDateTime(final ZonedDateTime zonedDateTime) {
        return zonedDateTime != null ? zonedDateTime.withZoneSameInstant(DEFAULT_ZONE_ID).toLocalDateTime() : null;
    }
}
