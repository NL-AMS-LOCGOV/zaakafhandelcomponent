/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.model;

import net.atos.zac.zaaksturing.model.FormulierDefinition;

public class RESTFormulierDefinition {
    public String naam;

    public String id;

    public RESTFormulierDefinition() {
    }

    public RESTFormulierDefinition(final FormulierDefinition formulierDefinition) {
        this.naam = formulierDefinition.getNaam();
        this.id = formulierDefinition.name();
    }

    public FormulierDefinition getFormulierDefinition() {
        return FormulierDefinition.valueOf(id);
    }
}
