/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.converter;

import java.util.List;

import net.atos.zac.app.admin.model.RESTReferentieTabelWaarde;
import net.atos.zac.zaaksturing.model.ReferentieTabelWaarde;

public class RESTReferentieWaardeConverter {

    public RESTReferentieTabelWaarde convert(final ReferentieTabelWaarde referentieTabelWaarde) {
        final RESTReferentieTabelWaarde restReferentieTabelWaarde = new RESTReferentieTabelWaarde();
        restReferentieTabelWaarde.id = referentieTabelWaarde.getId();
        restReferentieTabelWaarde.naam = referentieTabelWaarde.getNaam();
        return restReferentieTabelWaarde;
    }

    public List<String> convert(final List<ReferentieTabelWaarde> referentieTabelWaarden) {
        return referentieTabelWaarden.stream()
                .map(ReferentieTabelWaarde::getNaam)
                .toList();
    }

    public ReferentieTabelWaarde convert(final RESTReferentieTabelWaarde restReferentieTabelWaarde) {
        final ReferentieTabelWaarde referentieTabelWaarde = new ReferentieTabelWaarde();
        referentieTabelWaarde.setId(restReferentieTabelWaarde.id);
        referentieTabelWaarde.setNaam(restReferentieTabelWaarde.naam);
        return referentieTabelWaarde;
    }
}
