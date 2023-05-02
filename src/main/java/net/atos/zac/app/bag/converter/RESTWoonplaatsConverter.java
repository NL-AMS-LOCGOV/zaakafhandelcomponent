/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.bag.converter;

import java.net.URI;

import net.atos.client.bag.model.Indicatie;
import net.atos.client.bag.model.Woonplaats;
import net.atos.client.bag.model.WoonplaatsIOHal;
import net.atos.client.bag.model.WoonplaatsIOHalBasis;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.zaakobjecten.ObjectWoonplaats;
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectWoonplaats;
import net.atos.zac.app.bag.model.RESTWoonplaats;

public class RESTWoonplaatsConverter {

    public RESTWoonplaats convertToREST(final WoonplaatsIOHalBasis woonplaatsIO) {
        if (woonplaatsIO == null) {
            return null;
        }
        final RESTWoonplaats restWoonplaats = convertToREST(woonplaatsIO.getWoonplaats());
        restWoonplaats.url = URI.create(woonplaatsIO.getLinks().getSelf().getHref());
        return restWoonplaats;
    }

    public RESTWoonplaats convertToREST(final WoonplaatsIOHal woonplaatsIO) {
        if (woonplaatsIO == null) {
            return null;
        }
        final RESTWoonplaats restWoonplaats = convertToREST(woonplaatsIO.getWoonplaats());
        restWoonplaats.url = URI.create(woonplaatsIO.getLinks().getSelf().getHref());
        return restWoonplaats;
    }

    public RESTWoonplaats convertToREST(final ZaakobjectWoonplaats zaakobjectWoonplaats) {
        if (zaakobjectWoonplaats == null || zaakobjectWoonplaats.getObjectIdentificatie() == null) {
            return null;
        }
        final ObjectWoonplaats woonplaats = zaakobjectWoonplaats.getObjectIdentificatie();
        final RESTWoonplaats restWoonplaats = new RESTWoonplaats();
        restWoonplaats.url = zaakobjectWoonplaats.getObject();
        restWoonplaats.identificatie = woonplaats.getIdentificatie();
        restWoonplaats.naam = woonplaats.getWoonplaatsNaam();
        return restWoonplaats;
    }

    public ZaakobjectWoonplaats convertToZaakobject(final RESTWoonplaats woonplaats, final Zaak zaak) {
        return new ZaakobjectWoonplaats(zaak.getUrl(), woonplaats.url, new ObjectWoonplaats(woonplaats.identificatie, woonplaats.naam));
    }

    private RESTWoonplaats convertToREST(final Woonplaats woonplaats) {
        final RESTWoonplaats restWoonplaats = new RESTWoonplaats();
        restWoonplaats.identificatie = woonplaats.getIdentificatie();
        restWoonplaats.naam = woonplaats.getNaam();
        restWoonplaats.status = woonplaats.getStatus();
        restWoonplaats.geconstateerd = Indicatie.J.equals(woonplaats.getGeconstateerd());
        return restWoonplaats;
    }

}
