/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.model;

import java.time.LocalDate;

public class RESTGerelateerdeZaak {

    public enum RelatieType {
        DEELZAAK,
        HOOFDZAAK,
        VERVOLG,
        RELEVANT,
        BIJDRAGE
    }

    public RelatieType relatieType;

    public String identificatie;

    public String omschrijving;

    public String toelichting;

    public LocalDate startdatum;

    public LocalDate einddatum;

    public String uuid;

}
