/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.drc.model;

import javax.json.bind.annotation.JsonbTypeAdapter;

import net.atos.client.zgw.shared.model.AbstractEnum;

@JsonbTypeAdapter(ObjectType.Adapter.class)
public enum ObjectType implements AbstractEnum<ObjectType> {

    BESLUIT("besluit"),

    ZAAK("zaak");

    private final String value;

    ObjectType(final String value) {
        this.value = value;
    }

    @Override
    public String toValue() {
        return value;
    }

    public static ObjectType fromValue(final String value) {
        return AbstractEnum.fromValue(values(), value);
    }

    static class Adapter extends AbstractEnum.Adapter<ObjectType> {

        @Override
        protected ObjectType[] getEnums() {
            return values();
        }
    }
}
