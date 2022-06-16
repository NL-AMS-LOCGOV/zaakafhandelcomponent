/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.converter;

import java.util.List;

import net.atos.zac.app.admin.model.RESTZaakbeeindigReden;
import net.atos.zac.zaaksturing.model.ZaakbeeindigReden;

public class RESTZaakbeeindigRedenConverter {

    public RESTZaakbeeindigReden convertZaakbeeindigReden(final ZaakbeeindigReden zaakbeeindigReden) {
        final RESTZaakbeeindigReden restZaakbeeindigReden = new RESTZaakbeeindigReden();
        restZaakbeeindigReden.id = zaakbeeindigReden.getId();
        restZaakbeeindigReden.naam = zaakbeeindigReden.getNaam();
        return restZaakbeeindigReden;
    }

    public List<RESTZaakbeeindigReden> convertZaakbeeindigRedenen(final List<ZaakbeeindigReden> zaakbeeindigRedenen) {
        return zaakbeeindigRedenen.stream()
                .map(this::convertZaakbeeindigReden)
                .toList();
    }

    public ZaakbeeindigReden convertRESTZaakbeeindigReden(final RESTZaakbeeindigReden restZaakbeeindigReden) {
        final ZaakbeeindigReden zaakbeeindigReden = new ZaakbeeindigReden();
        zaakbeeindigReden.setId(restZaakbeeindigReden.id);
        zaakbeeindigReden.setNaam(restZaakbeeindigReden.naam);
        return zaakbeeindigReden;
    }
}
