/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.model;

import java.net.URI;
import java.time.LocalDate;

public class RESTBesluit {

    public URI url;

    public String identificatie;

    public LocalDate datum;

    public RESTBesluittype besluittype;

    public LocalDate ingangsdatum;

    public LocalDate vervaldatum;

    public String toelichting;

}
