/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken.model;

import java.time.LocalDate;

public class RESTDatumRange {

    public LocalDate van;

    public LocalDate tot;

    public boolean hasValue() {
        return this.van != null || this.tot != null;
    }

}
