/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zaaksturing;

import javax.enterprise.context.ApplicationScoped;

import net.atos.zac.zaaksturing.model.TaakFormulieren;

@ApplicationScoped
public class ZaakSturingService {

    public TaakFormulieren findTaakFormulieren(final String zaaktypeIdentificatie, final String planItemDefinitionId) {
        return createTaakFormulieren();
    }

    private TaakFormulieren createTaakFormulieren() {
        final TaakFormulieren taakFormulieren = new TaakFormulieren();
        taakFormulieren.setPlanItmeDefinitionId("_planItemDefinitionId_");
        taakFormulieren.setStartFormulier("_startFormulier_");
        taakFormulieren.setBehandelFormulier("_behandelFormulier_");
        return taakFormulieren;
    }
}
