/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.planitems.model;

import net.atos.zac.zaaksturing.model.FormulierDefinitie;

import java.util.Arrays;

public enum HumanTaskFormulierKoppeling {

    AANVULLENDE_INFORMATIE("aamvullende_info", FormulierDefinitie.AANVULLENDE_INFORMATIE),
    GOEDKEUREN("goedkeuren", FormulierDefinitie.GOEDKEUREN),
    ADVIES_INTERN("advies_intern", FormulierDefinitie.ADVIES),
    ADVIES_EXTERN("advies_extern", FormulierDefinitie.EXTERN_ADVIES_VASTLEGGEN),
    DEFAULT("", FormulierDefinitie.DEFAULT_TAAKFORMULIER);

    private final String planItemDefinitionId;
    private final FormulierDefinitie formulierDefinitie;

    HumanTaskFormulierKoppeling(final String planItemDefinitionId, final FormulierDefinitie formulierDefinitie) {
        this.planItemDefinitionId = planItemDefinitionId;
        this.formulierDefinitie = formulierDefinitie;
    }

    public String getPlanItemDefinitionId() {
        return planItemDefinitionId;
    }

    public FormulierDefinitie getFormulierDefinitie() {
        return formulierDefinitie;
    }

    public static FormulierDefinitie readFormulierDefinitie(final String planItemDefinitionId) {
        return Arrays.stream(values())
                .filter(humanTaskFormulierKoppeling -> humanTaskFormulierKoppeling.planItemDefinitionId.equals(planItemDefinitionId))
                .map(HumanTaskFormulierKoppeling::getFormulierDefinitie)
                .findAny().orElse(HumanTaskFormulierKoppeling.DEFAULT.getFormulierDefinitie());
    }
}
