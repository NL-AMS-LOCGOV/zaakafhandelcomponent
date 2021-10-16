/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.model;

import java.util.Arrays;

import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbTypeAdapter;

import org.apache.commons.lang3.StringUtils;

/**
 *
 */
@JsonbTypeAdapter(Archiefnominatie.Adapter.class)
public enum Archiefnominatie {

    /**
     * Het zaakdossier moet bewaard blijven en op de Archiefactiedatum overgedragen worden naar een archiefbewaarplaats.
     */
    BLIJVEND_BEWAREN("blijvend_bewaren"),

    /**
     * Het zaakdossier moet op of na de Archiefactiedatum vernietigd worden.
     */
    VERNIETIGEN("vernietigen");

    private final String value;

    Archiefnominatie(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Archiefnominatie fromValue(final String value) {
        return Arrays.stream(values())
                .filter(archiefnominatie -> archiefnominatie.value.equals(value))
                .findAny()
                .orElseThrow();
    }

    static class Adapter implements JsonbAdapter<Archiefnominatie, String> {

        @Override
        public String adaptToJson(final Archiefnominatie archiefnominatie) {
            return archiefnominatie.value;
        }

        @Override
        public Archiefnominatie adaptFromJson(final String json) {
            return StringUtils.isNotBlank(json) ? fromValue(json) : null;
        }
    }
}
