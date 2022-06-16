/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.converter;

import java.util.List;

import net.atos.client.zgw.ztc.model.Resultaattype;
import net.atos.zac.app.admin.model.RESTZaakResultaattype;
import net.atos.zac.util.UriUtil;

public class RESTZaakResultaattypeConverter {

    public RESTZaakResultaattype convertResultaattype(final Resultaattype resultaattype) {
        final RESTZaakResultaattype restZaakResultaattype = new RESTZaakResultaattype();
        restZaakResultaattype.id = UriUtil.uuidFromURI(resultaattype.getUrl());
        restZaakResultaattype.naam = resultaattype.getOmschrijving();
        return restZaakResultaattype;
    }

    public List<RESTZaakResultaattype> convertResultaattypes(final List<Resultaattype> resultaattypes) {
        return resultaattypes.stream()
                .map(this::convertResultaattype)
                .toList();
    }
}
