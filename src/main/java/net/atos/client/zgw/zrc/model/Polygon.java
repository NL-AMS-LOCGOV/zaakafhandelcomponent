/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import java.util.List;

/**
 *
 */
public class Polygon extends Geometry {

    private final List<List<Point2D>> coordinates;

    public Polygon(final List<List<Point2D>> coordinates) {
        super("Polygon");
        this.coordinates = coordinates;
    }

    public List<List<Point2D>> getCoordinates() {
        return coordinates;
    }
}
