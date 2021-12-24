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
@JsonbTypeAdapter(Status.Adapter.class)
public enum Status {

    PUBLISHED("published"),
    DRAFT("draft"),
    DEPRECATED("deprecated");

    private final String value;

    Status(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    static class Adapter implements JsonbAdapter<Status, String> {

        @Override
        public String adaptToJson(final Status status) {
            return status.value;
        }

        @Override
        public Status adaptFromJson(final String json) {
            if (StringUtils.isBlank(json)) {
                return null;
            }
            return stream(values())
                    .filter(status -> StringUtils.equals(status.value, json))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(String.format("Unkown value for %s: '%s'", Status.class.getSimpleName(), json)));
        }
    }
}
