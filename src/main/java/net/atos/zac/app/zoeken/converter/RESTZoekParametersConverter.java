/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken.converter;

import net.atos.zac.app.zoeken.model.RESTZoekParameters;
import net.atos.zac.zoeken.model.ZoekParameters;
import net.atos.zac.zoeken.model.ZoekVeld;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

public class RESTZoekParametersConverter {

    public ZoekParameters convert(final RESTZoekParameters restZoekParameters) {
        final ZoekParameters zoekParameters = new ZoekParameters(ZoekObjectType.ZAAK);

        zoekParameters.setTekstZoekVeld(ZoekVeld.ALLE, restZoekParameters.tekst);
        zoekParameters.setStart(restZoekParameters.start);
        zoekParameters.setRows(restZoekParameters.rows);
        return zoekParameters;
    }
}
