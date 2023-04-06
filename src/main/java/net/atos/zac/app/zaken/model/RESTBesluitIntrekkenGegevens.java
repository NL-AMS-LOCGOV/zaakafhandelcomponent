/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.model;

import java.time.LocalDate;
import java.util.UUID;

public class RESTBesluitIntrekkenGegevens {

    public UUID besluitUuid;

    public LocalDate vervaldatum;

    public String vervalreden;

    public String reden;
}
