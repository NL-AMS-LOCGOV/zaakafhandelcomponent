/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.model;

import java.time.LocalDate;

public class RESTGerelateerdeZaak {

    public RelatieType relatieType;

    public String zaaknummer;

    public String zaaktype;

    public String status;

    public LocalDate startdatum;
}
