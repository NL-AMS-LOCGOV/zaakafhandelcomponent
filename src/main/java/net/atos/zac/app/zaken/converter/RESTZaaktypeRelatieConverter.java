/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import java.net.URI;

import net.atos.client.zgw.ztc.model.ZaakTypenRelatie;
import net.atos.zac.app.zaken.model.RESTZaaktypeRelatie;
import net.atos.zac.app.zaken.model.RelatieType;
import net.atos.zac.util.UriUtil;

public class RESTZaaktypeRelatieConverter {

    public RESTZaaktypeRelatie convertToRESTZaaktypeRelatie(final URI zaaktypUri, final RelatieType relatieType) {
        final RESTZaaktypeRelatie restZaaktypeRelatie = new RESTZaaktypeRelatie();
        restZaaktypeRelatie.zaaktypeUuid = UriUtil.uuidFromURI(zaaktypUri);
        restZaaktypeRelatie.relatieType = relatieType;
        return restZaaktypeRelatie;
    }

    public RESTZaaktypeRelatie convertToRESTZaaktypeRelatie(final ZaakTypenRelatie zaakTypenRelatie) {
        final RESTZaaktypeRelatie restZaaktypeRelatie = new RESTZaaktypeRelatie();
        restZaaktypeRelatie.zaaktypeUuid = UriUtil.uuidFromURI(zaakTypenRelatie.getZaaktype());
        restZaaktypeRelatie.relatieType = RelatieType.valueOf(zaakTypenRelatie.getAardRelatie().name());
        return restZaaktypeRelatie;
    }
}
