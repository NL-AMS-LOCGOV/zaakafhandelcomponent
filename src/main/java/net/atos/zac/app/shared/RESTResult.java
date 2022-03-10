/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.shared;

import java.util.Collection;

public class RESTResult<TYPE> {

    Collection<TYPE> results;

    int count;

    public RESTResult(final Collection<TYPE> results, final int count) {
        this.results = results;
        this.count = count;
    }

    public Collection<TYPE> getResults() {
        return results;
    }

    public int getCount() {
        return count;
    }
}
