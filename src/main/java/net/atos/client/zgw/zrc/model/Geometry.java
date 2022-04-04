/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import javax.json.bind.annotation.JsonbTypeDeserializer;

import net.atos.client.zgw.zrc.util.GeometryJsonbDeserializer;

/**
 *
 */
@JsonbTypeDeserializer(GeometryJsonbDeserializer.class)
public abstract class Geometry {
    public static final String GEOMETRY_TYPE_NAAM = "type";

    private final GeometryType type;

    protected Geometry(final GeometryType type) {
        this.type = type;
    }

    public Geometry() {
        type = null;
    }

    public GeometryType getType() {
        return type;
    }
}
