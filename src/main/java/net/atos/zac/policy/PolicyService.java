/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.policy;

import javax.enterprise.context.ApplicationScoped;

import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.policy.model.ZaakActies;

@ApplicationScoped
public class PolicyService {

    public ZaakActies readZaakActies(final Zaak zaak) {
        return new ZaakActies(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
    }
}
