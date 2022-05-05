/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken.converter;

import net.atos.zac.app.zoeken.model.RESTSolrZoekParameters;
import net.atos.zac.zoeken.model.SolrZoekParameters;

public class RESTSolrZoekParametersConverter {

    public SolrZoekParameters convert(final RESTSolrZoekParameters restZoekParameters) {
        final SolrZoekParameters zoekParameters = new SolrZoekParameters();
        zoekParameters.setTekst(restZoekParameters.tekst);
        return zoekParameters;
    }
}
