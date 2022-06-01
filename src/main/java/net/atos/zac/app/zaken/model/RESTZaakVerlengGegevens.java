/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.model;

import java.time.LocalDate;

public class RESTZaakVerlengGegevens {

    public String redenVerlenging;

    public int duurDagen;

    public boolean takenVerlengen;

    public LocalDate einddatumGepland;

    public LocalDate uiterlijkeEinddatumAfdoening;
}
