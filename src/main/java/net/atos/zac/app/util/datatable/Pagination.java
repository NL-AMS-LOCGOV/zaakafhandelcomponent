/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.util.datatable;

import javax.json.bind.annotation.JsonbProperty;

public class Pagination {

    @JsonbProperty("pageNumber")
    private int pageNumber;

    @JsonbProperty("pageSize")
    private int pageSize;

    public Pagination() {
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(final int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }

    public int getFirstResult() {
        return pageNumber * pageSize;
    }

    @Override
    public String toString() {
        return "Pagination{pageNumber=" + pageNumber + ", pageSize=" + pageSize + '}';
    }
}
