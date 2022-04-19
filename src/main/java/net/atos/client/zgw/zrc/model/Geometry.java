/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import javax.json.bind.annotation.JsonbTypeDeserializer;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @Override
    public abstract String toString();

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Geometry geometry = (Geometry) o;

        return new EqualsBuilder().append(type, geometry.type).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(type).toHashCode();
    }
}
