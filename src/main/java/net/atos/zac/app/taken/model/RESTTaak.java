/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.taken.model;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.atos.zac.app.identity.model.RESTGroup;
import net.atos.zac.app.identity.model.RESTUser;
import net.atos.zac.zaaksturing.model.FormulierDefinitie;

/**
 *
 */
public class RESTTaak {

    public String id;

    public String naam;

    public String toelichting;

    public ZonedDateTime creatiedatumTijd;

    // Datum waarop de taak is toegekend aan een behandelaar
    public ZonedDateTime toekenningsdatumTijd;

    public LocalDate streefdatum;

    public RESTUser behandelaar;

    public RESTGroup groep;

    public UUID zaakUUID;

    public String zaakIdentificatie;

    public String zaaktypeOmschrijving;

    public TaakStatus status;

    public FormulierDefinitie formulierDefinitie;

    public Map<String, String> taakdata;

    public Map<String, String> taakinformatie;

    public List<UUID> taakdocumenten;
}
