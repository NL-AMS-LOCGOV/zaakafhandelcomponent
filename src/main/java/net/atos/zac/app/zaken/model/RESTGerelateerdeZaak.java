/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.model;

import java.time.LocalDate;

public class RESTGerelateerdeZaak {

    public RelatieType relatieType;

    public String identificatie;

    public String zaaktypeOmschrijving;

    public String statustypeOmschrijving;

    public LocalDate startdatum;
}
