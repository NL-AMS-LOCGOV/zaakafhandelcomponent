/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken.converter;

import java.util.ArrayList;
import java.util.List;

import net.atos.zac.app.zoeken.model.AbstractRESTZoekObject;
import net.atos.zac.app.zoeken.model.RESTZoekParameters;
import net.atos.zac.app.zoeken.model.RESTZoekResultaat;
import net.atos.zac.zoeken.model.TaakZoekObject;
import net.atos.zac.zoeken.model.ZaakZoekObject;
import net.atos.zac.zoeken.model.ZoekObject;
import net.atos.zac.zoeken.model.ZoekResultaat;

public class RESTZoekResultaatConverter {

    public RESTZoekResultaat<? extends AbstractRESTZoekObject> convert(
            final ZoekResultaat<? extends ZoekObject> zoekResultaat, final RESTZoekParameters zoekParameters) {

        final RESTZoekResultaat<? extends AbstractRESTZoekObject> restZoekResultaat =
                new RESTZoekResultaat<>(zoekResultaat.getItems().stream().map(this::convert).toList(), zoekResultaat.getCount());

        restZoekResultaat.filters.putAll(zoekResultaat.getFilters());
        zoekResultaat.getFilters().forEach((filterVeld, mogelijkeFilters) -> {
            //indien geen resultaten, de huidige filters laten staan
            final String zoekFilter = zoekParameters.filters.get(filterVeld);
            if (zoekFilter != null && !mogelijkeFilters.contains(zoekFilter)) {
                final List<String> filters = new ArrayList<>(mogelijkeFilters);
                filters.add(zoekFilter);
                restZoekResultaat.filters.put(filterVeld, filters);
            }
        });
        return restZoekResultaat;
    }

    private AbstractRESTZoekObject convert(final ZoekObject zoekObject) {
        return switch (zoekObject.getType()) {
            case ZAAK -> RESTZaakZoekObjectConverter.convert((ZaakZoekObject) zoekObject);
            case TAAK -> RESTTaakZoekObjectConverter.convert((TaakZoekObject) zoekObject);
        };
    }
}
