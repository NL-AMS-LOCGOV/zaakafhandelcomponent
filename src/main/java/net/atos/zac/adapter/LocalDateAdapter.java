/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.adapter;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.json.bind.adapter.JsonbAdapter;

import org.apache.commons.lang3.StringUtils;

public class LocalDateAdapter implements JsonbAdapter<LocalDate, String> {

    @Override
    public String adaptToJson(final LocalDate datum) {
        return datum != null ? datum.format(DateTimeFormatter.ISO_DATE) : null;
    }

    @Override
    public LocalDate adaptFromJson(final String datum) {
        if (StringUtils.isBlank(datum)) {
            return null;
        } else if (StringUtils.containsAny(datum, "+", "T", "Z")) {
            //zone niet aanpassen aan locale tijdzone (withZoneSameInstant(ZoneId.of("Europe/Amsterdam")))
            return ZonedDateTime.parse(datum).toLocalDate();
        } else {
            return LocalDate.parse(datum);
        }
    }
}
