/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.documentcreatie.model;

import static net.atos.zac.documentcreatie.converter.DataConverter.DATE_FORMAT;

import java.time.LocalDate;

import javax.json.bind.annotation.JsonbDateFormat;

public class ZaakData {

    public String zaaktype;

    public String identificatie;

    public String omschrijving;

    public String toelichting;

    @JsonbDateFormat(DATE_FORMAT)
    public LocalDate registratiedatum;

    @JsonbDateFormat(DATE_FORMAT)
    public LocalDate startdatum;

    @JsonbDateFormat(DATE_FORMAT)
    public LocalDate einddatumGepland;

    @JsonbDateFormat(DATE_FORMAT)
    public LocalDate uiterlijkeEinddatumAfdoening;

    @JsonbDateFormat(DATE_FORMAT)
    public LocalDate einddatum;

    public String communicatiekanaal;

    public String vertrouwelijkheidaanduiding;

    public String verlengingReden;

    public String opschortingReden;

    public String resultaat;

    public String status;

    public String besluit;

    public String groep;

    public String behandelaar;
}
