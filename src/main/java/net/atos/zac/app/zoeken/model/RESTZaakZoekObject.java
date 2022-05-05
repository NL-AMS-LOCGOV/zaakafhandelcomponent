/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken.model;

import java.time.LocalDate;
import java.util.UUID;

public class RESTZaakZoekObject {

    public String type;

    public UUID uuid;

    public String identificatie;

    public LocalDate startdatum;

    public LocalDate registratiedatum;

    public LocalDate einddatum;

    public LocalDate streefdatum;

    public LocalDate fataledatum;

    public String zaaktypeNaam;

    public String statusNaam;

    public String resultaatNaam;

    public String omschrijving;

    public String toelichting;

    public String locatie;

    public String behandelaarNaam;

    public String groepNaam;

}
