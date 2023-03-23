/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.bag.converter;

import java.net.URI;

import net.atos.client.bag.model.AdresIOHal;
import net.atos.client.bag.model.Indicatie;
import net.atos.client.bag.model.OpenbareRuimte;
import net.atos.client.bag.model.OpenbareRuimteIOHalBasis;
import net.atos.zac.app.bag.model.RESTOpenbareRuimte;

public class RESTOpenbareRuimteConverter {

    public RESTOpenbareRuimte convertToREST(final OpenbareRuimteIOHalBasis openbareRuimteIO, final AdresIOHal adres) {
        if (openbareRuimteIO == null) {
            return null;
        }
        final RESTOpenbareRuimte restOpenbareRuimte = convertToREST(openbareRuimteIO);
        restOpenbareRuimte.woonplaatsNaam = adres != null ? adres.getWoonplaatsNaam() : openbareRuimteIO.getOpenbareRuimte().getLigtIn();
        return restOpenbareRuimte;
    }

    public RESTOpenbareRuimte convertToREST(final OpenbareRuimteIOHalBasis openbareRuimteIO) {
        if (openbareRuimteIO == null) {
            return null;
        }
        final OpenbareRuimte openbareRuimte = openbareRuimteIO.getOpenbareRuimte();
        final RESTOpenbareRuimte restOpenbareRuimte = new RESTOpenbareRuimte();
        restOpenbareRuimte.url = URI.create(openbareRuimteIO.getLinks().getSelf().getHref());
        restOpenbareRuimte.identificatie = openbareRuimte.getIdentificatie();
        restOpenbareRuimte.naam = openbareRuimte.getNaam();
        restOpenbareRuimte.status = openbareRuimte.getStatus();
        restOpenbareRuimte.type = openbareRuimte.getType();
        restOpenbareRuimte.geconstateerd = Indicatie.J.equals(openbareRuimte.getGeconstateerd());
        return restOpenbareRuimte;
    }

}
