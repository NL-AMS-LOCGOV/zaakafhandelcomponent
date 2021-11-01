/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.datatable;

import java.util.Map;

import javax.json.bind.annotation.JsonbProperty;

public class Search {

    @JsonbProperty("predicateObject")
    private Map<String, String> predicateObject;

    public Search() {
    }

    public Map<String, String> getPredicateObject() {
        return predicateObject;
    }

    public void setPredicateObject(final Map<String, String> predicateObject) {
        this.predicateObject = predicateObject;
    }

    @Override
    public String toString() {
        return "Search{" + "predicateObject=" + predicateObject + '}';
    }
}
