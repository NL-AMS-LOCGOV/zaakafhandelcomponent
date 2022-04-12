/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.datatable;

import static net.atos.zac.util.JsonbUtil.JSONB;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import javax.json.bind.annotation.JsonbProperty;
import javax.servlet.http.HttpServletRequest;

public class TableRequest {

    @JsonbProperty("sort")
    private Sort sort;

    @JsonbProperty("search")
    private Search search;

    @JsonbProperty("pagination")
    private Pagination pagination;

    public TableRequest() {
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(final Sort sort) {
        this.sort = sort;
    }

    public Search getSearch() {
        return search;
    }

    public void setSearch(final Search search) {
        this.search = search;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(final Pagination pagination) {
        this.pagination = pagination;
    }

    public static TableRequest getTableState(final HttpServletRequest request) {
        if (request.getQueryString() != null) {
            //in de getQueryString ()is "tableRequest=" voor de querystring geplakt
            //deze er eerst afhalen voor het mappen
            final String decodeQueryString = URLDecoder.decode(request.getQueryString(), StandardCharsets.UTF_8).substring(13);
            return JSONB.fromJson(decodeQueryString, TableRequest.class);
        }

        return new TableRequest();
    }

    @Override
    public String toString() {
        return "TableState{" + "\n" + "sort=" + sort + "\n" + ", search=" + search + "\n" + ", pagination=" + pagination + "\n" + '}';
    }

}

