/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.model;

import java.time.LocalDate;

import net.atos.zac.app.identity.model.RESTGroep;

public class RESTPlanItemParameters {
    public String planItemId;

    public String formulierDefinitieId;

    public LocalDate streefdatum;

    public RESTGroep defaultGroep;

    public RESTPlanItemParameters(final String planItemId, final String schermId) {
        this.planItemId = planItemId;
        this.formulierDefinitieId = schermId;
    }
}
