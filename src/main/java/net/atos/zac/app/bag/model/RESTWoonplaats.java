/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.bag.model;

import net.atos.client.bag.model.StatusWoonplaats;

public class RESTWoonplaats extends RESTBAGObject {

    public String naam;

    public StatusWoonplaats status;

    public RESTWoonplaats() {
    }

    @Override
    public BAGObjectType getBagObjectType() {
        return BAGObjectType.WOONPLAATS;
    }

    @Override
    public String getOmschrijving() {
        return naam;
    }
}
