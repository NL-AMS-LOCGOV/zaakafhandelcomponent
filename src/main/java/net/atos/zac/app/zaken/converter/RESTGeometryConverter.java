/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.atos.client.zgw.zrc.model.Geometry;
import net.atos.client.zgw.zrc.model.GeometryCollection;
import net.atos.client.zgw.zrc.model.GeometryType;
import net.atos.client.zgw.zrc.model.Point;
import net.atos.client.zgw.zrc.model.Point2D;
import net.atos.client.zgw.zrc.model.Polygon;
import net.atos.zac.app.zaken.model.RESTCoordinates;
import net.atos.zac.app.zaken.model.RESTGeometry;

public class RESTGeometryConverter {


    public RESTGeometry convert(final Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        final RESTGeometry restGeometry = new RESTGeometry();
        restGeometry.type = geometry.getType().toValue();
        switch (geometry.getType()) {
            case POINT -> restGeometry.point = new RESTCoordinates(((Point) geometry).getCoordinates().getX().doubleValue(),
                                                                   ((Point) geometry).getCoordinates().getY().doubleValue());
            case POLYGON -> restGeometry.polygon = ((Polygon) geometry).getCoordinates().stream()
                    .map(point2DS -> point2DS.stream()
                            .map(point2D -> new RESTCoordinates(point2D.getX().doubleValue(), point2D.getY().doubleValue()))
                            .collect(Collectors.toList()))
                    .collect(Collectors.toList());
            case GEOMETRYCOLLECTION -> restGeometry.geometrycollection =
                    ((GeometryCollection) geometry).getGeometries().stream()
                            .map(geometry1 -> convert(geometry))
                            .collect(Collectors.toList());
            default -> throw new IllegalStateException("Unexpected value: " + geometry.getType());
        }

        return restGeometry;
    }

    public Geometry convert(final RESTGeometry restGeometry) {
        if (restGeometry == null) {
            return null;
        }
        final Geometry geometry;
        switch (GeometryType.fromValue(restGeometry.type)) {
            case POINT -> geometry = createPoint(restGeometry);
            case POLYGON -> geometry = createPolygon(restGeometry);
            case GEOMETRYCOLLECTION -> geometry = createGeometryCollection(restGeometry);
            default -> throw new IllegalStateException("Unexpected value: " + restGeometry.type);
        }

        return geometry;
    }

    private Point createPoint(final RESTGeometry restGeometry) {
        final Point2D point2D = new Point2D(new BigDecimal(restGeometry.point.x), new BigDecimal(restGeometry.point.y));
        return new Point(point2D);
    }

    private Polygon createPolygon(final RESTGeometry restGeometry) {
        final List<List<Point2D>> polygonCoordinates = restGeometry.polygon.stream()
                .map(polygon -> polygon.stream()
                        .map(coordinates -> new Point2D(new BigDecimal(coordinates.x), new BigDecimal(coordinates.y)))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
        return new Polygon(polygonCoordinates);
    }

    private GeometryCollection createGeometryCollection(final RESTGeometry restGeometry) {
        final List<Geometry> collection = new ArrayList<>();
        for (final RESTGeometry geometry : restGeometry.geometrycollection) {
            collection.add(convert(geometry));
        }
        return new GeometryCollection(collection);
    }
}
