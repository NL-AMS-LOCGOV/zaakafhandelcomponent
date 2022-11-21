/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken.model;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import net.atos.zac.app.shared.RESTResultaat;
import net.atos.zac.zoeken.model.FilterResultaat;
import net.atos.zac.zoeken.model.FilterVeld;

public class RESTZoekResultaat<TYPE> extends RESTResultaat<TYPE> {

    public TreeMap<FilterVeld, List<FilterResultaat>> filters = new TreeMap<>(Comparator.comparingInt(FilterVeld::ordinal));

    public RESTZoekResultaat(final Collection<TYPE> resultaten, final long aantalTotaal) {
        super(resultaten, aantalTotaal);
    }

    public RESTZoekResultaat(final String foutmelding) {
        super(foutmelding);
    }

}
