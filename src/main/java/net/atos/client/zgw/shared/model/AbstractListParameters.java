/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.model;

import javax.ws.rs.QueryParam;

/**
 *
 */
public abstract class AbstractListParameters {

    /**
     * Een pagina binnen de gepagineerde set resultaten.
     */
    @QueryParam("page")
    private Integer page;

    public Integer getPage() {
        return page;
    }

    public void setPage(final Integer page) {
        this.page = page;
    }
}
