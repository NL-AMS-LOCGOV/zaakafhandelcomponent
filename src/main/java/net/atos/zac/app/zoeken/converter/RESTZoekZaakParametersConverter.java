/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken.converter;

import net.atos.zac.app.zoeken.model.RESTZoekZaakParameters;
import net.atos.zac.zoeken.model.ZaakZoekParameters;

public class RESTZoekZaakParametersConverter {

    public ZaakZoekParameters convert(final RESTZoekZaakParameters restZoekZaakParameters) {
        final ZaakZoekParameters zoekZaakParameters = new ZaakZoekParameters();
        zoekZaakParameters.setTekst(restZoekZaakParameters.tekst);
        return zoekZaakParameters;
    }
}
