/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.bag.converter;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.zaakobjecten.Zaakobject;
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectAdres;
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectNummeraanduiding;
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectOpenbareRuimte;
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectPand;
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectWoonplaats;
import net.atos.zac.app.bag.model.RESTAdres;
import net.atos.zac.app.bag.model.RESTBAGObject;
import net.atos.zac.app.bag.model.RESTNummeraanduiding;
import net.atos.zac.app.bag.model.RESTOpenbareRuimte;
import net.atos.zac.app.bag.model.RESTPand;
import net.atos.zac.app.bag.model.RESTWoonplaats;

public class RESTBAGConverter {

    @Inject
    private RESTAdresConverter adresConverter;

    @Inject
    private RESTNummeraanduidingConverter nummeraanduidingConverter;

    @Inject
    private RESTOpenbareRuimteConverter openbareRuimteConverter;

    @Inject
    private RESTPandConverter pandConverter;

    @Inject
    private RESTWoonplaatsConverter woonplaatsConverter;

    public Zaakobject convertToZaakobject(final RESTBAGObject restbagObject, final Zaak zaak) {
        return switch (restbagObject.getBagObjectType()) {
            case ADRES -> adresConverter.convertToZaakobject((RESTAdres) restbagObject, zaak);
            case PAND -> pandConverter.convertToZaakobject((RESTPand) restbagObject, zaak);
            case WOONPLAATS -> woonplaatsConverter.convertToZaakobject((RESTWoonplaats) restbagObject, zaak);
            case OPENBARE_RUIMTE -> openbareRuimteConverter.convertToZaakobject((RESTOpenbareRuimte) restbagObject, zaak);
            case NUMMERAANDUIDING -> nummeraanduidingConverter.convertToZaakobject((RESTNummeraanduiding) restbagObject, zaak);
        };
    }

    public RESTBAGObject convertToBAGObject(final Zaakobject zaakobject) {
        return switch (zaakobject.getObjectType()) {
            case ADRES -> adresConverter.convertToREST((ZaakobjectAdres) zaakobject);
            case PAND -> pandConverter.convertToREST((ZaakobjectPand) zaakobject);
            case WOONPLAATS -> woonplaatsConverter.convertToREST((ZaakobjectWoonplaats) zaakobject);
            case OPENBARE_RUIMTE -> openbareRuimteConverter.convertToREST((ZaakobjectOpenbareRuimte) zaakobject);
            case OVERIGE -> nummeraanduidingConverter.convertToREST((ZaakobjectNummeraanduiding) zaakobject); // voor nu alleen nummeraanduiding
            default -> throw new IllegalStateException("Unexpected objectType: " + zaakobject.getObjectType());
        };
    }

    public static String getHuisnummerWeergave(final Integer huisnummer, final String huisletter, final String huisnummertoevoeging) {
        final StringBuilder volledigHuisnummer = new StringBuilder();
        volledigHuisnummer.append(huisnummer);

        if (StringUtils.isNotBlank(huisletter)) {
            volledigHuisnummer.append(huisletter);
        }
        if (StringUtils.isNotBlank(huisnummertoevoeging)) {
            volledigHuisnummer.append("-").append(huisnummertoevoeging);
        }
        return volledigHuisnummer.toString().trim();
    }
}
