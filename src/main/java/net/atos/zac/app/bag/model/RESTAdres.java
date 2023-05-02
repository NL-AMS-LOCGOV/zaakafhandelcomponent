/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.bag.model;

import java.util.ArrayList;
import java.util.List;

public class RESTAdres extends RESTBAGObject {

    public String postcode;

    public String huisnummerWeergave;

    public int huisnummer;

    public String huisletter;

    public String huisnummertoevoeging;

    public String openbareRuimteNaam;

    public String woonplaatsNaam;

    public RESTOpenbareRuimte openbareRuimte;

    public RESTNummeraanduiding nummeraanduiding;

    public RESTWoonplaats woonplaats;

    public RESTAdresseerbaarObject adresseerbaarObject;

    public List<RESTPand> panden = new ArrayList<>();

    public RESTAdres() {
    }

    @Override
    public BAGObjectType getBagObjectType() {
        return BAGObjectType.ADRES;
    }

    @Override
    public String getOmschrijving() {
        return "%s %s, %s %s".formatted(openbareRuimteNaam, huisnummerWeergave, postcode, woonplaatsNaam);
    }
}
