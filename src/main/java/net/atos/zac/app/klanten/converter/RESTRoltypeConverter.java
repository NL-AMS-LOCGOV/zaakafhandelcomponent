/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.klanten.converter;

import java.util.List;
import java.util.stream.Stream;

import net.atos.client.zgw.ztc.model.Roltype;
import net.atos.zac.app.klanten.model.klant.RESTRoltype;

public class RESTRoltypeConverter {

    public List<RESTRoltype> convert(final Stream<Roltype> roltypen) {
        return roltypen.map(this::convert).toList();
    }

    public RESTRoltype convert(final Roltype roltype) {
        final RESTRoltype restRoltype = new RESTRoltype();
        restRoltype.uuid = roltype.getUUID();
        restRoltype.naam = roltype.getOmschrijving();
        restRoltype.aardvanrol = roltype.getOmschrijvingGeneriek();
        return restRoltype;
    }
}
