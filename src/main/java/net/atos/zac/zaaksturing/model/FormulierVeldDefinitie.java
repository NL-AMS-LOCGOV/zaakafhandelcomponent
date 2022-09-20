/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zaaksturing.model;

public enum FormulierVeldDefinitie {
    ADVIES(ReferentieTabel.Systeem.ADVIES);

    private final ReferentieTabel.Systeem defaultTabel;

    FormulierVeldDefinitie(final ReferentieTabel.Systeem defaultTabel) {
        this.defaultTabel = defaultTabel;
    }

    public ReferentieTabel.Systeem getDefaultTabel() {
        return defaultTabel;
    }
}
