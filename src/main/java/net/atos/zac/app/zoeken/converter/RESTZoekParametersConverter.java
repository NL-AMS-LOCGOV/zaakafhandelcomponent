/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken.converter;

import net.atos.zac.app.zoeken.model.RESTZoekParameters;
import net.atos.zac.zoeken.model.ZoekParameters;

public class RESTZoekParametersConverter {

    public ZoekParameters convert(final RESTZoekParameters restZoekParameters) {
        final ZoekParameters zoekParameters = new ZoekParameters();
        zoekParameters.setTekst(restZoekParameters.tekst);
        return zoekParameters;
    }
}
