/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.model;

import net.atos.zac.app.planitems.model.DefaultHumanTaskFormulierKoppeling;
import net.atos.zac.app.planitems.model.PlanItemType;
import net.atos.zac.zaaksturing.model.FormulierDefinitie;

public class RESTPlanItemDefinition {

    public String id;

    public String naam;

    public PlanItemType type;

    public FormulierDefinitie defaultFormulierDefinitie;

    public RESTPlanItemDefinition() {
    }

    public RESTPlanItemDefinition(final String id, final String naam, final PlanItemType type) {
        this.id = id;
        this.naam = naam;
        this.type = type;
        this.defaultFormulierDefinitie = DefaultHumanTaskFormulierKoppeling.readFormulierDefinitie(id);
    }
}
