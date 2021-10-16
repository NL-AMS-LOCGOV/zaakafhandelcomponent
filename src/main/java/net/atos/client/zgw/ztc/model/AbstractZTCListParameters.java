/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.ztc.model;

import javax.ws.rs.QueryParam;

/**
 *
 */
public abstract class AbstractZTCListParameters {

    /*
     * Filter objects depending on their concept status
     */
    private ObjectStatusFilter status;

    @QueryParam("status")
    public String getStatus() {
        return status != null ? status.toValue() : null;
    }

    public void setStatus(final ObjectStatusFilter status) {
        this.status = status;
    }
}
