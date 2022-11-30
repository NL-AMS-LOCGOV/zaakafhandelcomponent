/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.configuratie.converter;

import java.util.List;
import java.util.stream.Collectors;

import net.atos.zac.app.configuratie.model.RESTTaal;
import net.atos.zac.configuratie.model.Taal;

public class RESTTaalConverter {

    public RESTTaal convert(final Taal taal) {
        final RESTTaal restTaal = new RESTTaal();
        restTaal.id = Long.toString(taal.getId());
        restTaal.code = taal.getCode();
        restTaal.naam = taal.getNaam();
        restTaal.name = taal.getName();
        restTaal.local = taal.getLocal();
        return restTaal;
    }

    public List<RESTTaal> convert(final List<Taal> talen) {
        return talen.stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }
}
