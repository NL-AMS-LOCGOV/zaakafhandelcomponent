/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zaaksturing.model;

import java.util.List;

public class ZaakSturingParameters {

    private String zaaktypeIdentificatie;

    private List<TaakFormulieren> taakFormulieren;

    public String getZaaktypeIdentificatie() {
        return zaaktypeIdentificatie;
    }

    public void setZaaktypeIdentificatie(final String zaaktypeIdentificatie) {
        this.zaaktypeIdentificatie = zaaktypeIdentificatie;
    }

    public List<TaakFormulieren> getTaakFormulieren() {
        return taakFormulieren;
    }

    public void setTaakFormulieren(final List<TaakFormulieren> taakFormulieren) {
        this.taakFormulieren = taakFormulieren;
    }
}
