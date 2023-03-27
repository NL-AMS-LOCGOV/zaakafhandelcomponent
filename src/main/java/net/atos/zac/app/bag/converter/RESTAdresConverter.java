/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.bag.converter;

import java.net.URI;

import javax.inject.Inject;

import org.apache.commons.lang3.BooleanUtils;

import net.atos.client.bag.model.AdresIOHal;
import net.atos.client.bag.model.Geconstateerd;
import net.atos.zac.app.bag.model.RESTAdres;

public class RESTAdresConverter {

    @Inject
    private RESTOpenbareRuimteConverter openbareRuimteConverter;

    @Inject
    private RESTNummeraanduidingConverter nummeraanduidingConverter;

    @Inject
    private RESTPandConverter pandConverter;

    @Inject
    private RESTWoonplaatsConverter woonplaatsConverter;

    public RESTAdres convertToREST(final AdresIOHal adres) {
        if (adres == null) {
            return null;
        }
        final RESTAdres restAdres = new RESTAdres();
        restAdres.url = URI.create(adres.getLinks().getSelf().getHref());
        restAdres.identificatie = adres.getAdresseerbaarObjectIdentificatie();
        restAdres.postcode = adres.getPostcode();
        restAdres.huisnummerWeergave = convertToVolledigHuisnummer(adres);
        restAdres.openbareRuimteNaam = adres.getOpenbareRuimteNaam();
        restAdres.woonplaatsNaam = adres.getWoonplaatsNaam();
        if (adres.getGeconstateerd() != null) {
            final Geconstateerd geconstateerd = adres.getGeconstateerd();
            restAdres.geconstateerd =
                    BooleanUtils.isTrue(geconstateerd.getNummeraanduiding()) &&
                            BooleanUtils.isTrue(geconstateerd.getWoonplaats()) &&
                            BooleanUtils.isTrue(geconstateerd.getOpenbareRuimte());
        }

        if (adres.getEmbedded() != null) {
            restAdres.openbareRuimte = openbareRuimteConverter.convertToREST(adres.getEmbedded().getOpenbareRuimte(), adres);
            restAdres.nummeraanduiding = nummeraanduidingConverter.convertToREST(adres.getEmbedded().getNummeraanduiding());
            restAdres.woonplaats = woonplaatsConverter.convertToREST(adres.getEmbedded().getWoonplaats());
            restAdres.panden = pandConverter.convertToRest(adres.getEmbedded().getPanden());
        }
        return restAdres;
    }

    private String convertToVolledigHuisnummer(final AdresIOHal adresHal) {
        return RESTBAGConverterUtil.getHuisnummerWeergave(adresHal.getHuisnummer(), adresHal.getHuisletter(), adresHal.getHuisnummertoevoeging());
    }
}
