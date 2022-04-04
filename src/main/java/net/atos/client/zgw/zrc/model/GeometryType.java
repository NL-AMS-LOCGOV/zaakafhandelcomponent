/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import javax.json.bind.annotation.JsonbTypeAdapter;

import net.atos.client.zgw.shared.model.AbstractEnum;


@JsonbTypeAdapter(GeometryType.Adapter.class)
public enum GeometryType implements AbstractEnum<GeometryType> {
    POINT("Point"),
    POLYGON("Polygon"),
    GEOMETRYCOLLECTION("GeometryCollection");

    private final String value;

    GeometryType(final String value) {
        this.value = value;
    }

    @Override
    public String toValue() {
        return value;
    }

    public static GeometryType fromValue(final String value) {
        return AbstractEnum.fromValue(values(), value);
    }

    static class Adapter extends AbstractEnum.Adapter<GeometryType> {
        @Override
        protected GeometryType[] getEnums() {
            return values();
        }
    }
}
