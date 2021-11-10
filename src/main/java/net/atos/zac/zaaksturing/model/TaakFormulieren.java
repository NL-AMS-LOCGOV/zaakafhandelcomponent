/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zaaksturing.model;

public class TaakFormulieren {

    private String planItmeDefinitionId;

    private String startFormulier;

    private String behandelFormulier;

    public String getPlanItmeDefinitionId() {
        return planItmeDefinitionId;
    }

    public void setPlanItmeDefinitionId(final String planItmeDefinitionId) {
        this.planItmeDefinitionId = planItmeDefinitionId;
    }

    public String getStartFormulier() {
        return startFormulier;
    }

    public void setStartFormulier(final String startFormulier) {
        this.startFormulier = startFormulier;
    }

    public String getBehandelFormulier() {
        return behandelFormulier;
    }

    public void setBehandelFormulier(final String behandelFormulier) {
        this.behandelFormulier = behandelFormulier;
    }
}
