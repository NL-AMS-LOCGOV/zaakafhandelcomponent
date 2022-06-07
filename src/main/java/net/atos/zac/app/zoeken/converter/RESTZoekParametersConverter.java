/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken.converter;

import net.atos.zac.app.zoeken.model.RESTZoekParameters;
import net.atos.zac.shared.model.SorteerRichting;
import net.atos.zac.zoeken.model.DatumRange;
import net.atos.zac.zoeken.model.ZoekParameters;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

public class RESTZoekParametersConverter {

    public ZoekParameters convert(final RESTZoekParameters restZoekParameters) {
        final ZoekParameters zoekParameters = new ZoekParameters(ZoekObjectType.ZAAK);

        restZoekParameters.filters.forEach(zoekParameters::addFilter);

        restZoekParameters.datums.forEach((key, value) -> {
            if (value != null && value.hasValue()) {
                zoekParameters.addDatum(key, new DatumRange(value.van, value.tot));
            }
        });

        restZoekParameters.filterQueries.forEach(zoekParameters::addFilterQuery);

        if (restZoekParameters.zoeken != null) {
            restZoekParameters.zoeken.forEach(zoekParameters::addZoekVeld);
        }

        if (restZoekParameters.sorteerVeld != null) {
            zoekParameters.setSortering(restZoekParameters.sorteerVeld, SorteerRichting.fromValue(restZoekParameters.sorteerRichting));
        }

        zoekParameters.setStart(restZoekParameters.page * restZoekParameters.rows);
        zoekParameters.setRows(restZoekParameters.rows);
        return zoekParameters;
    }
}
