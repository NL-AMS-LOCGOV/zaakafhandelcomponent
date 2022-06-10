/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util.event;

public enum JobId {
    TAAK_SIGNALERINGEN_JOB("Taaksignaleringen verzenden"),
    ZAAK_SIGNALERINGEN_JOB("Zaaksignaleringen verzenden");

    private final String name;

    JobId(final String value) {
        this.name = value;
    }

    public String getName() {
        return name;
    }
}
