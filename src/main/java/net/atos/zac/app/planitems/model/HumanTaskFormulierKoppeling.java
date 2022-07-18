/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.planitems.model;

import java.util.Arrays;

import net.atos.zac.zaaksturing.model.FormulierDefinitie;

public enum HumanTaskFormulierKoppeling {

    AANVULLENDE_INFORMATIE("AANVULLENDE_INFORMATIE", FormulierDefinitie.AANVULLENDE_INFORMATIE),
    GOEDKEUREN("GOEDKEUREN", FormulierDefinitie.GOEDKEUREN),
    ADVIES_INTERN("ADVIES_INTERN", FormulierDefinitie.ADVIES),
    ADVIES_EXTERN("ADVIES_EXTERN", FormulierDefinitie.EXTERN_ADVIES_VASTLEGGEN),
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
