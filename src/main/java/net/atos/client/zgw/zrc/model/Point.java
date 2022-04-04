/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

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
}
