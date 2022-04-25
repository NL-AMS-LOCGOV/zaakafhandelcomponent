/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.model;

import net.atos.zac.zaaksturing.model.FormulierDefinitie;

public class RESTFormulierDefinitie {

    public String id;

    public RESTFormulierDefinitie() {
    }

    public RESTFormulierDefinitie(final FormulierDefinitie formulierDefinitie) {
        this.id = formulierDefinitie.toString();
    }

    public FormulierDefinitie getFormulierDefinitie() {
        return FormulierDefinitie.valueOf(id);
    }
}
