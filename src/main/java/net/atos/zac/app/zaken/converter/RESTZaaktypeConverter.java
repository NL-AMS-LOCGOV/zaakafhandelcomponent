/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.app.zaken.model.RESTZaaktype;
import net.atos.zac.util.UriUtil;

public class RESTZaaktypeConverter {

    public static RESTZaaktype convert(final Zaaktype zaaktype) {
        final RESTZaaktype restZaaktype = new RESTZaaktype();
        restZaaktype.uuid = UriUtil.uuidFromURI(zaaktype.getUrl()).toString();
        restZaaktype.identificatie = zaaktype.getIdentificatie();
        restZaaktype.doel = zaaktype.getDoel();
        restZaaktype.omschrijving = zaaktype.getOmschrijving();
        return restZaaktype;
    }
}
