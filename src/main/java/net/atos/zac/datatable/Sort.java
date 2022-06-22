/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.datatable;

import net.atos.zac.shared.model.SorteerRichting;

public class Sort {

    private String predicate;

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

    public SorteerRichting getDirection() {
        return SorteerRichting.fromValue(direction);
    }

    public void setDirection(final String direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "Sort{" + "predicate='" + predicate + '\'' + ", direction=" + direction + '}';
    }
}
