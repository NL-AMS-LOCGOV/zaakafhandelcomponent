/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbTypeAdapter;

import org.apache.commons.lang3.NotImplementedException;

/**
 *
 */
@JsonbTypeAdapter(Point2D.Adapter.class)
public class Point2D {

    private final BigDecimal x;

    private final BigDecimal y;

    public Point2D(final BigDecimal x, final BigDecimal y) {
        this.x = x;
        this.y = y;
    }

    public BigDecimal getX() {
        return x;
    }

    public BigDecimal getY() {
        return y;
    }

    public static class Adapter implements JsonbAdapter<Point2D, List<BigDecimal>> {

        @Override
        public List<BigDecimal> adaptToJson(final Point2D point2D) {
            final List<BigDecimal> coordinates = new ArrayList<>();
            coordinates.add(point2D.x);
            coordinates.add(point2D.y);
            return coordinates;
        }

        @Override
        public Point2D adaptFromJson(final List<BigDecimal> coordinates) {
            throw new NotImplementedException(String.format("Deserializing '%s' object is not supported", Point2D.class.getSimpleName()));
        }
    }
}
