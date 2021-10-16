/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

/**
 *
 */
public class Point extends Geometry {

    private final Point2D coordinates;

    public Point(final Point2D coordinates) {
        super("Point");
        this.coordinates = coordinates;
    }

    public Point2D getCoordinates() {
        return coordinates;
    }
}
