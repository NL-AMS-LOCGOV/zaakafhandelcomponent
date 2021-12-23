/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.model;

import net.atos.zac.app.planitems.model.PlanItemType;

public class RESTPlanItemDefinitie {
    public String naam;

    public String id;

    public PlanItemType type;

    public RESTPlanItemDefinitie(final String naam, final String id, final PlanItemType type) {
        this.naam = naam;
        this.id = id;
        this.type = type;
    }
}
