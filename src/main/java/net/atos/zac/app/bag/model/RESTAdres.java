/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.bag.model;

import java.util.ArrayList;
import java.util.List;

import net.atos.zac.app.zaken.model.RESTGeometry;

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

    public RESTGeometry getGeometry() {
        RESTGeometry geometry = new RESTGeometry();
        geometry.type = "GeometryCollection";
        geometry.geometrycollection = new ArrayList<>();
        if (adresseerbaarObject != null && adresseerbaarObject.geometry != null) {
            geometry.geometrycollection.add(adresseerbaarObject.geometry);
        }
        if (panden != null && !panden.isEmpty() && panden.get(0).geometry != null) {
            geometry.geometrycollection.add(panden.get(0).geometry);
        }
        if (geometry.geometrycollection.size() == 1) {
            return geometry.geometrycollection.get(0);
        }
        if (geometry.geometrycollection.size() == 2) {
            return geometry;
        }
        return null;
    }
}
