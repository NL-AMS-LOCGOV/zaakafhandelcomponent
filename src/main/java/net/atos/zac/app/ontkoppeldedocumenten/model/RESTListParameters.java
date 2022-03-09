/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.ontkoppeldedocumenten.model;

import javax.ws.rs.QueryParam;

public class RESTListParameters {

    public RESTListParameters() {
    }

    @QueryParam("sort")
    public String sort;

    @QueryParam("order")
    public String order;

    @QueryParam("page")
    public int page;

    @QueryParam("maxResults")
    public int maxResults;
}
