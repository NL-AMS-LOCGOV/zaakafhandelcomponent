/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.model;

import java.time.LocalDate;

import net.atos.zac.app.identity.model.RESTGroep;

public class RESTPlanItemParameters {

    public String planItemDefinitionId;

    public String formulierDefinitieId;

    public LocalDate streefdatum;

    public RESTGroep defaultGroep;

    public RESTPlanItemParameters(final String planItemDefinitionId, final String formulierDefinitieId) {
        this.planItemDefinitionId = planItemDefinitionId;
        this.formulierDefinitieId = formulierDefinitieId;
    }
}
