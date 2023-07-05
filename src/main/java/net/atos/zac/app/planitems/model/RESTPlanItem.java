/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.planitems.model;

import java.time.LocalDate;
import java.util.UUID;


public class RESTPlanItem {

    public String id;

    public String naam;

    public PlanItemType type;

    public String groepId;

    public boolean actief;

    public String startformulierDefinitie;

    public String afhandelformulierDefinitie;

    public UUID zaakUuid;

    public UserEventListenerActie userEventListenerActie;

    public String toelichting;

    public LocalDate fataleDatum;

}
