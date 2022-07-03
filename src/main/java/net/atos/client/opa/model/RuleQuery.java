/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.opa.model;

public class RuleQuery<T> {

    private final T input;

    public RuleQuery(final T input) {
        this.input = input;
    }

    public T getInput() {
        return input;
    }
}
