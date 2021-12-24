/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.or.objecttype.model;

import static java.util.Arrays.stream;

import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbTypeAdapter;

import org.apache.commons.lang3.StringUtils;

/**
 *
 */
@JsonbTypeAdapter(Updatefrequency.Adapter.class)
public enum Updatefrequency {

    REAL_TIME("real_time"),
    HOURLY("hourly"),
    DAILY("daily"),
    WEEKLY("weekly"),
    MONTHLY("monthly"),
    YEARLY("yearly"),
    UNKNOWN("unknown");

    private final String value;

    Updatefrequency(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    static class Adapter implements JsonbAdapter<Updatefrequency, String> {

        @Override
        public String adaptToJson(final Updatefrequency updatefrequency) {
            return updatefrequency.value;
        }

        @Override
        public Updatefrequency adaptFromJson(final String json) {
            if (StringUtils.isBlank(json)) {
                return null;
            }
            return stream(values())
                    .filter(updatefrequency -> StringUtils.equals(updatefrequency.value, json))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(String.format("Unkown value for %s: '%s'", Updatefrequency.class.getSimpleName(), json)));
        }
    }
}
