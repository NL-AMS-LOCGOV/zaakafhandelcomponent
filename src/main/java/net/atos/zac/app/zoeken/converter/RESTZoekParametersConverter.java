/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken.converter;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.lang3.BooleanUtils;

import net.atos.zac.app.zoeken.model.RESTZoekParameters;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.shared.model.SorteerRichting;
import net.atos.zac.zoeken.model.DatumRange;
import net.atos.zac.zoeken.model.TaakZoekObject;
import net.atos.zac.zoeken.model.ZaakZoekObject;
import net.atos.zac.zoeken.model.ZoekParameters;

public class RESTZoekParametersConverter {

    @Inject
    private Instance<LoggedInUser> loggedInUserInstance;

    public ZoekParameters convert(final RESTZoekParameters restZoekParameters) {
        final ZoekParameters zoekParameters = new ZoekParameters(restZoekParameters.type);

        restZoekParameters.filters.forEach(zoekParameters::addFilter);

        restZoekParameters.datums.forEach((key, value) -> {
            if (value != null && value.hasValue()) {
                zoekParameters.addDatum(key, new DatumRange(value.van, value.tot));
            }
        });

        if (restZoekParameters.alleenOpenstaandeZaken) {
            zoekParameters.addFilterQuery(ZaakZoekObject.AFGEHANDELD_FIELD, BooleanUtils.FALSE);
        }

        if (restZoekParameters.alleenAfgeslotenZaken) {
            zoekParameters.addFilterQuery(ZaakZoekObject.AFGEHANDELD_FIELD, BooleanUtils.TRUE);
            zoekParameters.addFilterQuery(ZaakZoekObject.EINDSTATUS_FIELD, BooleanUtils.TRUE);
        }

        if (restZoekParameters.alleenMijnZaken) {
            zoekParameters.addFilterQuery(ZaakZoekObject.BEHANDELAAR_ID_FIELD, loggedInUserInstance.get().getId());
        }

        if (restZoekParameters.alleenMijnTaken) {
            zoekParameters.addFilterQuery(TaakZoekObject.BEHANDELAAR_ID_FIELD, loggedInUserInstance.get().getId());
        }

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
