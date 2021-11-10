/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.taken.model;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

import net.atos.zac.app.identity.model.RESTGroep;
import net.atos.zac.app.identity.model.RESTMedewerker;
import net.atos.zac.util.AbstractRESTObject;

/**
 *
 */
public class RESTTaak extends AbstractRESTObject {

    public String id;

    public String naam;

    public String toelichting;

    public ZonedDateTime creatiedatumTijd;

    // Datum waarop de taak is toegekend aan een behandelaar
    public ZonedDateTime toekenningsdatumTijd;

    public LocalDate streefdatum;

    public RESTMedewerker behandelaar;

    public RESTGroep groep;

    public UUID zaakUUID;

    public String zaakIdentificatie;

    public String zaaktypeOmschrijving;

    public TaakStatus status;

    public String taakBehandelFormulier;

    public Map<String, String> taakdata;
}
