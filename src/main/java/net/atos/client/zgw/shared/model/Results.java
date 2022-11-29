/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.model;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

/**
 *
 */
public class Results<T> {

    // Aantal items wat Open Zaak terug geeft per pagina
    public final static int NUM_ITEMS_PER_PAGE = 100;

    private final int count;

    private List<T> results;

    private final URI next;

    private final URI previous;

    public Results(final List<T> results, final int count) {
        this.results = results;
        this.count = count;
        previous = null;
        next = null;
    }

    @JsonbCreator
    public Results(@JsonbProperty("count") final int count,
            @JsonbProperty("results") final List<T> results,
            @JsonbProperty("next") final URI next,
            @JsonbProperty("previous") final URI previous) {
        this.count = count;
        this.results = results;
        this.next = next;
        this.previous = previous;
    }

    public int getCount() {
        return count;
    }

    public URI getNext() {
        return next;
    }

    public URI getPrevious() {
        return previous;
    }

    public List<T> getResults() {
        return results != null ? results : Collections.EMPTY_LIST;
    }

    public Optional<T> getSingleResult() {
        if (isEmpty(results)) {
            return Optional.empty();
        } else if (results.size() == 1) {
            return Optional.of(results.get(0));
        } else {
            throw new IllegalStateException(String.format("More then one result found (count: %d)", count));
        }
    }

    public List<T> getSinglePageResults() {
        if (next == null) {
            return getResults();
        } else {
            throw new IllegalStateException(String.format("More then one page found (count: %d, results: %d)", count, results.size()));
        }
    }
}
