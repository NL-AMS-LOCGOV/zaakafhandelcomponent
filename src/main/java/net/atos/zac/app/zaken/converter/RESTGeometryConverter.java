/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import java.util.List;

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
            case POINT -> restGeometry.point = createRESTPoint((Point) geometry);
            case POLYGON -> restGeometry.polygon = createRESTPolygon((Polygon) geometry);
            case GEOMETRYCOLLECTION -> restGeometry.geometrycollection = createRESTGeometryCollection((GeometryCollection) geometry);
            default -> throw new IllegalStateException("Unexpected value: " + geometry.getType());
        }

        return restGeometry;
    }

    public Geometry convert(final RESTGeometry restGeometry) {
        if (restGeometry == null) {
            return null;
        }

        return switch (GeometryType.fromValue(restGeometry.type)) {
            case POINT -> createPoint(restGeometry);
            case POLYGON -> createPolygon(restGeometry);
            case GEOMETRYCOLLECTION -> createGeometryCollection(restGeometry);
        };
    }

    private RESTCoordinates createRESTPoint(final Point point) {
        return new RESTCoordinates(point.getCoordinates().getX().doubleValue(),
                                   point.getCoordinates().getY().doubleValue());
    }

    private Point createPoint(final RESTGeometry restGeometry) {
        final Point2D point2D = new Point2D(restGeometry.point.x, restGeometry.point.y);
        return new Point(point2D);
    }

    private List<List<RESTCoordinates>> createRESTPolygon(final Polygon polygon) {
        return polygon.getCoordinates().stream()
                .map(point2DS -> point2DS.stream()
                        .map(point2D -> new RESTCoordinates(point2D.getX().doubleValue(), point2D.getY().doubleValue()))
                        .toList())
                .toList();
    }

    private Polygon createPolygon(final RESTGeometry restGeometry) {
        final List<List<Point2D>> polygonCoordinates = restGeometry.polygon.stream()
                .map(polygon -> polygon.stream()
                        .map(coordinates -> new Point2D(coordinates.x, coordinates.y))
                        .toList())
                .toList();
        return new Polygon(polygonCoordinates);
    }

    private List<RESTGeometry> createRESTGeometryCollection(final GeometryCollection geometryCollection) {
        return geometryCollection.getGeometries().stream()
                .map(geometry1 -> convert(geometryCollection))
                .toList();
    }

    private GeometryCollection createGeometryCollection(final RESTGeometry restGeometry) {
        final List<Geometry> collection = restGeometry.geometrycollection.stream().map(this::convert).toList();
        return new GeometryCollection(collection);
    }
}
