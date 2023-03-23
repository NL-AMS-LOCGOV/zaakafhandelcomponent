/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.bag.converter;


import java.net.URI;

import net.atos.client.bag.model.Indicatie;
import net.atos.client.bag.model.Nummeraanduiding;
import net.atos.client.bag.model.NummeraanduidingIOHalBasis;
import net.atos.zac.app.bag.model.RESTNummeraanduiding;

public class RESTNummeraanduidingConverter {

    public RESTNummeraanduiding convertToREST(final NummeraanduidingIOHalBasis nummeraanduidingIO) {
        if (nummeraanduidingIO == null) {
            return null;
        }
        final Nummeraanduiding nummeraanduiding = nummeraanduidingIO.getNummeraanduiding();
        final RESTNummeraanduiding restNummeraanduiding = new RESTNummeraanduiding();
        restNummeraanduiding.url = URI.create(nummeraanduidingIO.getLinks().getSelf().getHref());
        restNummeraanduiding.identificatie = nummeraanduiding.getIdentificatie();
        restNummeraanduiding.postcode = nummeraanduiding.getPostcode();
        restNummeraanduiding.huisnummer = nummeraanduiding.getHuisnummer();
        restNummeraanduiding.huisletter = nummeraanduiding.getHuisletter();
        restNummeraanduiding.huisnummertoevoeging = nummeraanduiding.getHuisnummertoevoeging();
        restNummeraanduiding.huisnummerWeergave = convertHuisnummerWeergave(nummeraanduiding);
        restNummeraanduiding.status = nummeraanduiding.getStatus();
        restNummeraanduiding.typeAdresseerbaarObject = nummeraanduiding.getTypeAdresseerbaarObject();
        restNummeraanduiding.geconstateerd = Indicatie.J.equals(nummeraanduiding.getGeconstateerd());
        return restNummeraanduiding;
    }

    private String convertHuisnummerWeergave(final Nummeraanduiding nummeraanduiding) {
        return RESTBAGConverterUtil.getHuisnummerWeergave(nummeraanduiding.getHuisnummer(), nummeraanduiding.getHuisletter(),
                                                          nummeraanduiding.getHuisnummertoevoeging());
    }
}
