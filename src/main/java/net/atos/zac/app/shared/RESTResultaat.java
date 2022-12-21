/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.shared;

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;

public class RESTResultaat<TYPE> {

    Collection<TYPE> resultaten;

    long totaal;

    String foutmelding;

    public RESTResultaat() {
        this.resultaten = Collections.emptyList();
        this.totaal = 0;
        this.foutmelding = StringUtils.EMPTY;
    }

    public RESTResultaat(final Collection<TYPE> resultaten, final long aantalTotaal) {
        this.resultaten = resultaten;
        this.totaal = aantalTotaal;
        this.foutmelding = StringUtils.EMPTY;
    }

    public RESTResultaat(final Collection<TYPE> resultaten) {
        this.resultaten = resultaten;
        this.totaal = resultaten.size();
        this.foutmelding = StringUtils.EMPTY;
    }

    public RESTResultaat(final String melding) {
        this.foutmelding = melding;
        this.resultaten = Collections.emptyList();
        this.totaal = 0;
    }

    public Collection<TYPE> getResultaten() {
        return resultaten;
    }

    public float getTotaal() {
        return totaal;
    }

    public String getFoutmelding() {
        return foutmelding;
    }
}
