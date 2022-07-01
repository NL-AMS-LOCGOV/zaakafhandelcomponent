/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.policy.model;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class RuleResponse<T> {

    private final T result;

    @JsonbCreator
    public RuleResponse(@JsonbProperty("result") final T result) {
        this.result = result;
    }

    public T getResult() {
        return result;
    }
}
