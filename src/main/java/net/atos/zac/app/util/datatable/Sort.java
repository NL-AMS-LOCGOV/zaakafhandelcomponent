/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.util.datatable;

import javax.json.bind.annotation.JsonbProperty;

public class Sort {

    @JsonbProperty("predicate")
    private String predicate;

    @JsonbProperty("direction")
    private String direction;

    /**
     * Default constructor
     */
    public Sort() {
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(final String predicate) {
        this.predicate = predicate;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(final String direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "Sort{" + "predicate='" + predicate + '\'' + ", direction=" + direction + '}';
    }
}
