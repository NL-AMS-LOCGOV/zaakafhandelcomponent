/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.converter;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import net.atos.client.zgw.ztc.model.Resultaattype;
import net.atos.zac.app.admin.model.RESTZaakResultaattype;
import net.atos.zac.util.UriUtil;

public class RESTZaakResultaattypeConverter {

    public RESTZaakResultaattype convertToRest(final Resultaattype resultaattype) {
        final RESTZaakResultaattype restZaakResultaattype = new RESTZaakResultaattype();
        restZaakResultaattype.id = UriUtil.uuidFromURI(resultaattype.getUrl()).toString();
        restZaakResultaattype.naam = resultaattype.getOmschrijving();
        return restZaakResultaattype;
    }

    public List<RESTZaakResultaattype> convertToRest(Collection<Resultaattype> resultaattypes) {
        return resultaattypes.stream()
                .map(this::convertToRest)
                .collect(Collectors.toList());
    }
}
