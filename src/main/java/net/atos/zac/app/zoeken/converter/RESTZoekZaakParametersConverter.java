/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken.converter;

import net.atos.zac.app.zoeken.model.RESTZoekZaakParameters;
import net.atos.zac.zoeken.model.ZoekZaakParameters;

public class RESTZoekZaakParametersConverter {

    public ZoekZaakParameters convert(final RESTZoekZaakParameters restZoekZaakParameters) {
        final ZoekZaakParameters zoekZaakParameters = new ZoekZaakParameters();
        zoekZaakParameters.setVrijeTekst(restZoekZaakParameters.vrijeTekst);
        return zoekZaakParameters;
    }
}
