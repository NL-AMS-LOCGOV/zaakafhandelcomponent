/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.taken.model;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.atos.zac.app.formulieren.model.RESTFormulierDefinitie;
import net.atos.zac.app.identity.model.RESTGroup;
import net.atos.zac.app.identity.model.RESTUser;
import net.atos.zac.app.policy.model.RESTTaakRechten;

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

    public LocalDate fataledatum;

    public RESTUser behandelaar;

    public RESTGroup groep;

    public UUID zaakUuid;

    public String zaakIdentificatie;

    public String zaaktypeOmschrijving;

    public TaakStatus status;

    // Identificatie van een vooraf gecodeerde combinatie van taak start en afhandel formulieren.
    // Deze worden enkel gebruikt door taken welke handmatig worden gestart vanuit een CMMN model
    public String formulierDefinitieId;

    // Definitie van een via de user interface gebouwd formulier.
    // Deze worden enkel gebruikt voor het afhandelen van taken welke automatische worden gestart vanuit een BPMN proces
    public RESTFormulierDefinitie formulierDefinitie;

    public Map<String, List<String>> tabellen = new HashMap<>();

    public Map<String, String> taakdata;

    public Map<String, String> taakinformatie;

    public List<UUID> taakdocumenten;

    public RESTTaakRechten rechten;
}
