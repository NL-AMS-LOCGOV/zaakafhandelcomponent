/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import java.util.List;

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
}
