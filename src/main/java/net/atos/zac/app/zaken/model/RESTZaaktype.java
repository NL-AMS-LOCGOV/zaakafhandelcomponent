/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.model;

import java.time.LocalDate;
import java.util.UUID;

public class RESTZaaktype {

    public UUID uuid;

    public String identificatie;

    public String doel;

    public String omschrijving;

    public String referentieproces;

    public boolean servicenorm;

    public LocalDate versiedatum;
}
