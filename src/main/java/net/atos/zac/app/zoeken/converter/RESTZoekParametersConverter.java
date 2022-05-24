/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken.converter;

import net.atos.zac.app.zoeken.model.RESTZoekParameters;
import net.atos.zac.zoeken.model.ZoekParameters;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

public class RESTZoekParametersConverter {

    public ZoekParameters convert(final RESTZoekParameters restZoekParameters) {
        final ZoekParameters zoekParameters = new ZoekParameters(ZoekObjectType.ZAAK);

        if (restZoekParameters.zoeken != null) {
            restZoekParameters.zoeken.forEach(zoekParameters::addZoekVeld);
        }
        zoekParameters.setStart(restZoekParameters.start);
        zoekParameters.setRows(restZoekParameters.rows);
        return zoekParameters;
    }
}
