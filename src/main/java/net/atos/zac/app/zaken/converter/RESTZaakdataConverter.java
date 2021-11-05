/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import net.atos.zac.app.zaken.model.RESTZaakdata;
import net.atos.zac.zaakdata.Zaakdata;

public class RESTZaakdataConverter {

    public RESTZaakdata convert(final Zaakdata zaakdata) {
        final RESTZaakdata restZaakdata = new RESTZaakdata();
        restZaakdata.elementen = zaakdata.getElementen();
        return restZaakdata;
    }

    public Zaakdata convert(final RESTZaakdata restZaakdata) {
        final Zaakdata zaakdata = new Zaakdata();
        zaakdata.setElementen(restZaakdata.elementen);
        return zaakdata;
    }
}
