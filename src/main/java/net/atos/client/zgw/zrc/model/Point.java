/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 *
 */
public class Point extends Geometry {

    private Point2D coordinates;

    public Point() {
        super(GeometryType.POINT);
    }

    public Point(final Point2D coordinates) {
        super(GeometryType.POINT);
        this.coordinates = coordinates;
    }

    public Point2D getCoordinates() {
        return coordinates;
    }

    @Override
    public String toString() {
        return String.format("POINT(%s %s)", getCoordinates().getX(), getCoordinates().getY());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Point point = (Point) o;

        return new EqualsBuilder().append(super.getType(), point.getType()).append(coordinates, point.coordinates).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(coordinates).toHashCode();
    }
}
