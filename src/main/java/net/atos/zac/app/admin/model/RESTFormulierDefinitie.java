/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.model;

import net.atos.zac.zaaksturing.model.FormulierDefinitie;

public class RESTFormulierDefinitie {
    public String naam;

    public String id;

    public RESTFormulierDefinitie() {
    }

    public RESTFormulierDefinitie(final FormulierDefinitie formulierDefinitie) {
        this.naam = formulierDefinitie.getNaam();
        this.id = formulierDefinitie.name();
    }

    public FormulierDefinitie getFormulierDefinitie() {
        return FormulierDefinitie.valueOf(id);
    }
}
