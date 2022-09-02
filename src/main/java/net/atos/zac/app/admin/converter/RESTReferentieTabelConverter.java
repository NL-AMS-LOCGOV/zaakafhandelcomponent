/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.converter;

import javax.inject.Inject;

import net.atos.zac.app.admin.model.RESTReferentieTabel;
import net.atos.zac.zaaksturing.model.ReferentieTabel;

public class RESTReferentieTabelConverter {

    @Inject
    private RESTReferentieWaardeConverter restReferentieWaardeConverter;

    public RESTReferentieTabel convert(final ReferentieTabel referentieTabel, boolean inclusiefWaarden) {
        final RESTReferentieTabel restReferentieTabel = new RESTReferentieTabel();
        restReferentieTabel.id = referentieTabel.getId();
        restReferentieTabel.code = referentieTabel.getCode();
        restReferentieTabel.naam = referentieTabel.getNaam();
        restReferentieTabel.systeem = referentieTabel.isSysteem();
        restReferentieTabel.aantalWaarden = referentieTabel.getWaarden().size();
        if (inclusiefWaarden) {
            restReferentieTabel.waarden = referentieTabel.getWaarden().stream()
                    .map(restReferentieWaardeConverter::convert)
                    .toList();
        }
        return restReferentieTabel;
    }

    public ReferentieTabel convert(final RESTReferentieTabel restReferentieTabel) {
        return convert(restReferentieTabel, new ReferentieTabel());
    }

    public ReferentieTabel convert(final RESTReferentieTabel restReferentieTabel, final ReferentieTabel referentieTabel) {
        referentieTabel.setCode(restReferentieTabel.code);
        referentieTabel.setNaam(restReferentieTabel.naam);
        referentieTabel.setWaarden(restReferentieTabel.waarden.stream()
                                           .map(restReferentieWaardeConverter::convert)
                                           .toList());
        return referentieTabel;
    }
}
