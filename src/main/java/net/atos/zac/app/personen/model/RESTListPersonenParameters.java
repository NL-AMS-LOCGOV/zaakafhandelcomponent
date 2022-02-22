/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.personen.model;

import java.time.LocalDate;

public class RESTListPersonenParameters {

    public String bsn;

    public String geslachtsnaam;

    public LocalDate geboortedatum;

    public String gemeenteVanInschrijving;

    public String postcode;

    public Integer huisnummer;
}
