/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.model;

import net.atos.zac.app.identity.model.RESTGroep;

public class RESTPlanItemParameters {

    public RESTPlanItemDefinition planItemDefinition;

    public RESTFormulierDefinitieVerwijzing formulierDefinitie;

    public Integer doorlooptijd;

    public RESTGroep defaultGroep;

    public RESTPlanItemParameters(final RESTPlanItemDefinition planItemDefinition, final RESTFormulierDefinitieVerwijzing formulierDefinitie) {
        this.planItemDefinition = planItemDefinition;
        this.formulierDefinitie = formulierDefinitie;
    }
}
