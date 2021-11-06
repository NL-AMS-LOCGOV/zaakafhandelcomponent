/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.drc.model;

import javax.json.bind.annotation.JsonbTypeAdapter;

import net.atos.client.zgw.shared.model.AbstractEnum;

@JsonbTypeAdapter(Objecttype.Adapter.class)
public enum Objecttype implements AbstractEnum<Objecttype> {

    BESLUIT("besluit"),

    ZAAK("zaak");

    private final String value;

    Objecttype(final String value) {
        this.value = value;
    }

    @Override
    public String toValue() {
        return value;
    }

    public static Objecttype fromValue(final String value) {
        return AbstractEnum.fromValue(values(), value);
    }

    static class Adapter extends AbstractEnum.Adapter<Objecttype> {

        @Override
        protected Objecttype[] getEnums() {
            return values();
        }
    }
}
