/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.audit.model;

import java.time.ZonedDateTime;
import java.util.UUID;

public class RESTAuditTrailRegel {

    public UUID uuid;

    public String applicatieId;

    public String applicatieWeergave;

    public String gebruikersId;

    public String actie;

    public String actieWeergave;

    public int httpStatusCode;

    public String resource;

    public UUID resourceID;

    public String resourceWeergave;

    public ZonedDateTime WijzigingsDatumTijd;

    public String toelichting;

    public RESTWijziging wijziging;

}
