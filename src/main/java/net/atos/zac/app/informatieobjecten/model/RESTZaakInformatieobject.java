/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten.model;

import java.time.LocalDate;

import net.atos.zac.app.policy.model.RESTZaakRechten;
import net.atos.zac.app.zaken.model.RESTZaakStatus;

/**
 * weergave van een zaak die is gekoppeld aan een (enkelvoudig) informatieobject
 */
public class RESTZaakInformatieobject {

    public String zaakIdentificatie;

    public RESTZaakStatus zaakStatus;

    public LocalDate zaakStartDatum;

    public LocalDate zaakEinddatumGepland;

    public String zaaktypeOmschrijving;

    public RESTZaakRechten zaakRechten;
}
