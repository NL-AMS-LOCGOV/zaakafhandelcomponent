/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.ztc.model;

import javax.json.bind.annotation.JsonbTypeAdapter;

import net.atos.client.zgw.shared.model.AbstractEnum;

/**
 *
 */
@JsonbTypeAdapter(Formaat.Adapter.class)
public enum Formaat implements AbstractEnum<Formaat> {

    TEKST("tekst"),

    GETAL("getal"),

    DATUM("datum"),

    DATUM_TIJD("datum_tijd");

    private final String value;

    Formaat(final String value) {
        this.value = value;
    }

    @Override
    public String toValue() {
        return value;
    }

    public static Formaat fromValue(final String value) {
        return AbstractEnum.fromValue(values(), value);
    }

    static class Adapter extends AbstractEnum.Adapter<Formaat> {

        @Override
        protected Formaat[] getEnums() {
            return values();
        }
    }
}
