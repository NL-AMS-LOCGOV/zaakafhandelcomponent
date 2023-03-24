/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.planitems.model;

import java.util.Arrays;
import java.util.Set;

import net.atos.zac.zaaksturing.model.FormulierDefinitie;
import net.atos.zac.zaaksturing.model.FormulierVeldDefinitie;

public enum DefaultHumanTaskFormulierKoppeling {

    AANVULLENDE_INFORMATIE("AANVULLENDE_INFORMATIE", FormulierDefinitie.AANVULLENDE_INFORMATIE),
    GOEDKEUREN("GOEDKEUREN", FormulierDefinitie.GOEDKEUREN),
    ADVIES_INTERN("ADVIES_INTERN", FormulierDefinitie.ADVIES),
    ADVIES_EXTERN("ADVIES_EXTERN", FormulierDefinitie.EXTERN_ADVIES_VASTLEGGEN),
    DOCUMENT_VERZENDEN_POST("DOCUMENT_VERZENDEN_POST", FormulierDefinitie.DOCUMENT_VERZENDEN_POST),
    DEFAULT("", FormulierDefinitie.DEFAULT_TAAKFORMULIER);

    private final String planItemDefinitionId;

    private final FormulierDefinitie formulierDefinitie;

    DefaultHumanTaskFormulierKoppeling(final String planItemDefinitionId, final FormulierDefinitie formulierDefinitie) {
        this.planItemDefinitionId = planItemDefinitionId;
        this.formulierDefinitie = formulierDefinitie;
    }

    public FormulierDefinitie getFormulierDefinitie() {
        return formulierDefinitie;
    }

    public static FormulierDefinitie readFormulierDefinitie(final String planItemDefinitionId) {
        return Arrays.stream(values())
                .filter(humanTaskFormulierKoppeling -> humanTaskFormulierKoppeling.planItemDefinitionId.equals(
                        planItemDefinitionId))
                .map(DefaultHumanTaskFormulierKoppeling::getFormulierDefinitie)
                .findAny()
                .orElse(DefaultHumanTaskFormulierKoppeling.DEFAULT.getFormulierDefinitie());
    }

    public static Set<FormulierVeldDefinitie> readFormulierVeldDefinities(final String planItemDefinitionId) {
        return readFormulierDefinitie(planItemDefinitionId).getVeldDefinities();
    }
}
