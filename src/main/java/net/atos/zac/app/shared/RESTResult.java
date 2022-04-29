/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.shared;

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;

public class RESTResult<TYPE> {

    Collection<TYPE> results;

    float count;

    String foutmelding;

    public RESTResult(final Collection<TYPE> results, final float count) {
        this.results = results;
        this.count = count;
        this.foutmelding = StringUtils.EMPTY;
    }

    public RESTResult(final Collection<TYPE> results) {
        this.results = results;
        this.count = results.size();
        this.foutmelding = StringUtils.EMPTY;
    }

    public RESTResult(final String melding) {
        this.foutmelding = melding;
        this.results = Collections.emptyList();
        this.count = 0;
    }

    public Collection<TYPE> getResults() {
        return results;
    }

    public float getCount() {
        return count;
    }

    public String getFoutmelding() {
        return foutmelding;
    }
}
