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
@JsonbTypeAdapter(Dataclassification.Adapter.class)
public enum Dataclassification {

    OPEN("open"),
    INTERN("intern"),
    CONFIDENTIAL("confidential"),
    STRICTLY_CONFIDENTIAL("strictly_confidential");

    private final String value;

    Dataclassification(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    static class Adapter implements JsonbAdapter<Dataclassification, String> {

        @Override
        public String adaptToJson(final Dataclassification dataclassification) {
            return dataclassification.value;
        }

        @Override
        public Dataclassification adaptFromJson(final String json) {
            if (StringUtils.isBlank(json)) {
                return null;
            }
            return stream(values())
                    .filter(dataclassification -> StringUtils.equals(dataclassification.value, json))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(String.format("Unkown value for %s: '%s'", Dataclassification.class.getSimpleName(), json)));
        }
    }

}
