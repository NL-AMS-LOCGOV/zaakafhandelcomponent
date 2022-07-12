/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.BooleanUtils;

import net.atos.client.zgw.shared.model.AbstractEnum;

public final class HistorieUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm").withZone(ZoneId.systemDefault());

    private static final String TRUE = "Ja";

    private static final String FALSE = "Nee";

    private HistorieUtil() {
    }

    public static String toWaarde(final LocalDate date) {
        return date != null ? DATE_FORMATTER.format(date) : null;
    }

    public static String toWaarde(final ZonedDateTime date) {
        return date != null ? DATE_TIME_FORMATTER.format(date) : null;
    }

    public static String toWaarde(final AbstractEnum<?> abstractEnum) {
        return abstractEnum != null ? abstractEnum.toValue() : null;
    }

    public static String toWaarde(final Boolean bool) {
        return bool != null ? BooleanUtils.toString(bool, TRUE, FALSE) : null;
    }
}
