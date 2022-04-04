/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.model;

public class RESTCoordinates {

    public RESTCoordinates() {
    }

    public RESTCoordinates(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    public double x;

    public double y;
}
