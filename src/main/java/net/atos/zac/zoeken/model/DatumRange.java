/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken.model;

import java.time.LocalDate;

public class DatumRange {
    public LocalDate van;

    public LocalDate tot;

    public DatumRange(final LocalDate van, final LocalDate tot) {
        this.van = van;
        this.tot = tot;
    }
}
