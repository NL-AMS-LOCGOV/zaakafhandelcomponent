/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.policy.input;

import net.atos.client.opa.model.RuleQuery;

@SuppressWarnings("InstantiationOfUtilityClass")
public class NoInput {
    public static final NoInput INPUT = new NoInput();

    public static final RuleQuery<NoInput> QUERY = new RuleQuery<>(INPUT);

    private NoInput() {
    }
}
