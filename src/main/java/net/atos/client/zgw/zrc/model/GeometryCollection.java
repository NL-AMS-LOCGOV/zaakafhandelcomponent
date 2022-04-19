/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 *
 */
public class GeometryCollection extends Geometry {

    private final List<Geometry> geometries;

    public GeometryCollection(final List<Geometry> geometries) {
        super(GeometryType.GEOMETRYCOLLECTION);
        this.geometries = geometries;
    }

    public List<Geometry> getGeometries() {
        return geometries;
    }

    @Override
    public String toString() {
        //TODO yet to be implemented
        return "GEOMETRYCOLLECTION()";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass() || !super.equals(o)) {
            return false;
        }

        final GeometryCollection that = (GeometryCollection) o;

        return new EqualsBuilder().append(geometries, that.geometries).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(geometries).toHashCode();
    }
}
