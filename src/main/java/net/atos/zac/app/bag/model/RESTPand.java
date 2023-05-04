/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.bag.model;

import net.atos.client.bag.model.StatusPand;
import net.atos.zac.app.zaken.model.RESTGeometry;

public class RESTPand extends RESTBAGObject {

    public String oorspronkelijkBouwjaar;

    public StatusPand status;

    public String statusWeergave;

    public RESTGeometry geometry;

    public RESTPand() {
    }

    @Override
    public BAGObjectType getBagObjectType() {
        return BAGObjectType.PAND;
    }

    @Override
    public String getOmschrijving() {
        return identificatie;
    }
}
