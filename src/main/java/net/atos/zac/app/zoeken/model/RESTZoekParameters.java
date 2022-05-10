/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken.model;

import javax.ws.rs.QueryParam;

public class RESTZoekParameters {

    @QueryParam("tekst")
    public String tekst;

    @QueryParam("rows")
    public int rows;

    @QueryParam("start")
    public int start;
}
