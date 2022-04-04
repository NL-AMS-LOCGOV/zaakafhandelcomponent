/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.model;

import java.util.List;

public class RESTGeometry {

    public String type;

    public RESTCoordinates point;

    public List<List<RESTCoordinates>> polygon;

    public List<RESTGeometry> geometrycollection;

}
