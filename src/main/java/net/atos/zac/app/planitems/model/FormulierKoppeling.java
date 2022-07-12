/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.planitems.model;

import net.atos.zac.zaaksturing.model.FormulierDefinitie;

public enum FormulierKoppeling {

    AANVULLENDE_INFORMATIE("aamvullende_info", FormulierDefinitie.AANVULLENDE_INFORMATIE),
    GOEDKEUREN("goedkeuren", FormulierDefinitie.GOEDKEUREN),
    ADVIES_INTERN("advies_intern", FormulierDefinitie.ADVIES),
    ADVIES_EXTERN("advies_extern", FormulierDefinitie.EXTERN_ADVIES_VASTLEGGEN),
    DEFAULT("", FormulierDefinitie.DEFAULT_TAAKFORMULIER);

    private final String planItemId;
    private final FormulierDefinitie formulierDefinitie;

    FormulierKoppeling(final String planItemId, final FormulierDefinitie formulierDefinitie) {
        this.planItemId = planItemId;
        this.formulierDefinitie = formulierDefinitie;
    }

    public String getPlanItemId() {
        return planItemId;
    }

    public FormulierDefinitie getFormulierDefinitie() {
        return formulierDefinitie;
    }

    public static FormulierDefinitie readFormulierDefinitie(final String planItemDefinitionId) {
        if (planItemDefinitionId.equals(FormulierKoppeling.GOEDKEUREN.getPlanItemId())) {
            return FormulierKoppeling.GOEDKEUREN.getFormulierDefinitie();
        } else if (planItemDefinitionId.equals(FormulierKoppeling.AANVULLENDE_INFORMATIE.getPlanItemId())) {
            return FormulierKoppeling.AANVULLENDE_INFORMATIE.getFormulierDefinitie();
        } else if (planItemDefinitionId.equals(FormulierKoppeling.ADVIES_EXTERN.getPlanItemId())) {
            return FormulierKoppeling.ADVIES_EXTERN.getFormulierDefinitie();
        } else if (planItemDefinitionId.equals(FormulierKoppeling.ADVIES_INTERN.getPlanItemId())) {
            return FormulierKoppeling.ADVIES_INTERN.getFormulierDefinitie();
        } else {
            return FormulierKoppeling.DEFAULT.getFormulierDefinitie();
        }
    }
}
