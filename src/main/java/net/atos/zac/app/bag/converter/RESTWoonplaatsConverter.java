/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.bag.converter;

import java.net.URI;

import net.atos.client.bag.model.Indicatie;
import net.atos.client.bag.model.Woonplaats;
import net.atos.client.bag.model.WoonplaatsIOHalBasis;
import net.atos.zac.app.bag.model.RESTWoonplaats;

public class RESTWoonplaatsConverter {

    public RESTWoonplaats convertToREST(final WoonplaatsIOHalBasis woonplaatsIO) {
        if (woonplaatsIO == null) {
            return null;
        }
        final Woonplaats woonplaats = woonplaatsIO.getWoonplaats();
        final RESTWoonplaats restWoonplaats = new RESTWoonplaats();
        restWoonplaats.url = URI.create(woonplaatsIO.getLinks().getSelf().getHref());
        restWoonplaats.identificatie = woonplaats.getIdentificatie();
        restWoonplaats.naam = woonplaats.getNaam();
        restWoonplaats.status = woonplaats.getStatus();
        restWoonplaats.geconstateerd = Indicatie.J.equals(woonplaats.getGeconstateerd());
        return restWoonplaats;
    }

}
