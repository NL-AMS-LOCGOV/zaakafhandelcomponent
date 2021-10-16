/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten.model;

import java.time.LocalDate;

import net.atos.zac.app.zaken.model.RESTZaakStatus;

/**
 * weergave van een zaak die is gekoppeld aan een (enkelvoudig) informatieobject
 */
public class RESTZaakInformatieObject {

    public RESTZaakStatus status;

    public String zaakUuid;

    public String zaakIdentificatie;

    public String zaaktype;

    public LocalDate zaakStartDatum;

    public LocalDate zaakEinddatumGepland;
}
