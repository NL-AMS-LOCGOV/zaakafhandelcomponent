/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.ztc.model;

import java.net.URI;

import javax.ws.rs.QueryParam;


public class ZaaktypeInformatieobjecttypeListParameters extends AbstractZTCListParameters {

    @QueryParam("zaaktype")
    private final URI zaaktype;

    public ZaaktypeInformatieobjecttypeListParameters(final URI zaaktype) {
        this.zaaktype = zaaktype;
    }

    public URI getZaaktype() {
        return zaaktype;
    }
}
