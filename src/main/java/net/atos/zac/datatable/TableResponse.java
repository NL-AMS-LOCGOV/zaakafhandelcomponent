/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.datatable;

import java.util.List;

public class TableResponse<REST> {

    private final List<REST> data;

    private final int totalItems;

    public TableResponse(final List<REST> data, final int totalItems) {
        this.data = data;
        this.totalItems = totalItems;
    }

    public List<REST> getData() {
        return data;
    }

    public int getTotalItems() {
        return totalItems;
    }
}
