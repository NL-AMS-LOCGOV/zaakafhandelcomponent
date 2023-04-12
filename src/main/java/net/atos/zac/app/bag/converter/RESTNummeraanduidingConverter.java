/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.bag.converter;


import java.net.URI;

import net.atos.client.bag.model.Indicatie;
import net.atos.client.bag.model.Nummeraanduiding;
import net.atos.client.bag.model.NummeraanduidingIOHalBasis;
import net.atos.client.bag.model.StatusNaamgeving;
import net.atos.client.bag.model.TypeAdresseerbaarObject;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.zaakobjecten.ObjectNummeraanduiding;
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectNummeraanduiding;
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

    public RESTNummeraanduiding convertToREST(final ZaakobjectNummeraanduiding zaakobjectNummeraanduiding) {
        if (zaakobjectNummeraanduiding == null || zaakobjectNummeraanduiding.getObjectIdentificatie() == null) {
            return null;
        }
        final ObjectNummeraanduiding nummeraanduiding = zaakobjectNummeraanduiding.getObjectIdentificatie().overigeData;
        final RESTNummeraanduiding restNummeraanduiding = new RESTNummeraanduiding();
        restNummeraanduiding.url = zaakobjectNummeraanduiding.getObject();
        restNummeraanduiding.identificatie = nummeraanduiding.getIdentificatie();
        restNummeraanduiding.postcode = nummeraanduiding.getPostcode();
        restNummeraanduiding.huisnummer = nummeraanduiding.getHuisnummer();
        restNummeraanduiding.huisletter = nummeraanduiding.getHuisletter();
        restNummeraanduiding.huisnummertoevoeging = nummeraanduiding.getHuisnummertoevoeging();
        restNummeraanduiding.huisnummerWeergave = convertHuisnummerWeergave(nummeraanduiding);
        restNummeraanduiding.status = StatusNaamgeving.fromValue(nummeraanduiding.getStatus());
        restNummeraanduiding.typeAdresseerbaarObject = TypeAdresseerbaarObject.fromValue(nummeraanduiding.getTypeAdresseerbaarObject());
        return restNummeraanduiding;
    }

    public ZaakobjectNummeraanduiding convertToZaakobject(final RESTNummeraanduiding nummeraanduiding, final Zaak zaak) {
        final ObjectNummeraanduiding objectNummeraanduiding = new ObjectNummeraanduiding(
                nummeraanduiding.identificatie,
                nummeraanduiding.huisnummer,
                nummeraanduiding.huisletter,
                nummeraanduiding.huisnummertoevoeging,
                nummeraanduiding.postcode,
                nummeraanduiding.typeAdresseerbaarObject != null ? nummeraanduiding.typeAdresseerbaarObject.toString() : null,
                nummeraanduiding.status != null ? nummeraanduiding.status.toString() : null
        );
        return new ZaakobjectNummeraanduiding(zaak.getUrl(), nummeraanduiding.url, objectNummeraanduiding);
    }

    private String convertHuisnummerWeergave(final Nummeraanduiding nummeraanduiding) {
        return RESTBAGConverter.getHuisnummerWeergave(nummeraanduiding.getHuisnummer(), nummeraanduiding.getHuisletter(),
                                                      nummeraanduiding.getHuisnummertoevoeging());
    }

    private String convertHuisnummerWeergave(final ObjectNummeraanduiding nummeraanduiding) {
        return RESTBAGConverter.getHuisnummerWeergave(nummeraanduiding.getHuisnummer(), nummeraanduiding.getHuisletter(),
                                                      nummeraanduiding.getHuisnummertoevoeging());
    }
}
