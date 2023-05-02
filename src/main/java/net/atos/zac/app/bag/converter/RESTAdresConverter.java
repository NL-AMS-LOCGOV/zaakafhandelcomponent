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
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.zaakobjecten.ObjectAdres;
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectAdres;
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

    @Inject
    private RESTAdreseerbaarObjectConverter adreseerbaarObjectConverter;

    public RESTAdres convertToREST(final AdresIOHal adres) {
        if (adres == null) {
            return null;
        }
        final RESTAdres restAdres = new RESTAdres();
        restAdres.url = URI.create(adres.getLinks().getSelf().getHref());
        restAdres.identificatie = adres.getNummeraanduidingIdentificatie();
        restAdres.postcode = adres.getPostcode();
        restAdres.huisnummer = adres.getHuisnummer();
        restAdres.huisletter = adres.getHuisletter();
        restAdres.huisnummertoevoeging = adres.getHuisnummertoevoeging();
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
            restAdres.panden = pandConverter.convertToREST(adres.getEmbedded().getPanden());
            restAdres.adresseerbaarObject = adreseerbaarObjectConverter.convertToREST(adres.getEmbedded().getAdresseerbaarObject());
        }
        return restAdres;
    }


    public RESTAdres convertToREST(final ZaakobjectAdres zaakobjectAdres) {
        if (zaakobjectAdres == null || zaakobjectAdres.getObjectIdentificatie() == null) {
            return null;
        }
        final ObjectAdres adres = zaakobjectAdres.getObjectIdentificatie();
        final RESTAdres restAdres = new RESTAdres();
        restAdres.url = zaakobjectAdres.getObject();
        restAdres.identificatie = adres.getIdentificatie();
        restAdres.postcode = adres.getPostcode();
        restAdres.huisnummerWeergave = convertToVolledigHuisnummer(adres);
        restAdres.openbareRuimteNaam = adres.getGorOpenbareRuimteNaam();
        restAdres.woonplaatsNaam = adres.getWplWoonplaatsNaam();
        return restAdres;
    }

    public ZaakobjectAdres convertToZaakobject(RESTAdres adres, final Zaak zaak) {
        ObjectAdres objectAdres = new ObjectAdres(adres.identificatie,
                                                  adres.woonplaatsNaam,
                                                  adres.openbareRuimteNaam,
                                                  adres.huisnummer,
                                                  adres.huisletter,
                                                  adres.huisnummertoevoeging,
                                                  adres.postcode);
        return new ZaakobjectAdres(zaak.getUrl(), adres.url, objectAdres);
    }

    private String convertToVolledigHuisnummer(final AdresIOHal adresHal) {
        return RESTBAGConverter.getHuisnummerWeergave(adresHal.getHuisnummer(), adresHal.getHuisletter(), adresHal.getHuisnummertoevoeging());
    }

    private String convertToVolledigHuisnummer(final ObjectAdres objectAdres) {
        return RESTBAGConverter.getHuisnummerWeergave(objectAdres.getHuisnummer(), objectAdres.getHuisletter(), objectAdres.getHuisnummertoevoeging());
    }
}
