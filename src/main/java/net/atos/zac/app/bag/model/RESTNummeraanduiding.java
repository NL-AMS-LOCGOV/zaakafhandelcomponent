/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.bag.model;

import net.atos.client.bag.model.StatusNaamgeving;
import net.atos.client.bag.model.TypeAdresseerbaarObject;

public class RESTNummeraanduiding extends RESTBAGObject {

    public String huisnummerWeergave;

    public int huisnummer;

    public String huisletter;

    public String huisnummertoevoeging;

    public String postcode;

    public TypeAdresseerbaarObject typeAdresseerbaarObject;

    public StatusNaamgeving status;

    public RESTWoonplaats woonplaats;

    public RESTOpenbareRuimte openbareRuimte;

    public RESTNummeraanduiding() {
    }

    @Override
    public BAGObjectType getBagObjectType() {
        return BAGObjectType.NUMMERAANDUIDING;
    }

    @Override
    public String getOmschrijving() {
        return "%s %s".formatted(huisnummerWeergave, postcode);
    }

}
