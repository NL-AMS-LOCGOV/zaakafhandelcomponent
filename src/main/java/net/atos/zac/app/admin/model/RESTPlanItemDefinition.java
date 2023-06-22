/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.model;


import net.atos.zac.app.planitems.model.PlanItemType;


public class RESTPlanItemDefinition {

    public String id;

    public String naam;

    public PlanItemType type;

    public RESTPlanItemDefinition() {
    }

    public RESTPlanItemDefinition(final String id, final String naam, final PlanItemType type) {
        this.id = id;
        this.naam = naam;
        this.type = type;
    }
}
