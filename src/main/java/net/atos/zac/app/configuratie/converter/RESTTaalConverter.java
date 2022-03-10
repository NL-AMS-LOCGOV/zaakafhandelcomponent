/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.configuratie.converter;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import net.atos.zac.app.configuratie.model.RESTTaal;
import net.atos.zac.configuratie.ConfiguratieService;
import net.atos.zac.configuratie.model.Taal;

public class RESTTaalConverter {

    @Inject
    private ConfiguratieService configuratieService;

    public RESTTaal convert(final String code) {
        return convert(configuratieService.findTaal(code));
    }

    public RESTTaal convert(final Taal taal) {
        if (taal != null) {
            final RESTTaal restTaal = new RESTTaal();
            restTaal.id = Long.toString(taal.getId());
            restTaal.code = taal.getCode();
            restTaal.naam = taal.getNaam();
            restTaal.name = taal.getName();
            restTaal.local = taal.getLocal();
            return restTaal;
        }
        return null;
    }

    public List<RESTTaal> convert(final List<Taal> talen) {
        return talen.stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }
}
