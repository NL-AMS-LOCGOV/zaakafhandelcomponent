/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.planitems.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.atos.zac.zaaksturing.model.FormulierDefinitie;

public class RESTPlanItem {

    public String id;

    public String naam;

    public PlanItemType type;

    public String groepId;

    public boolean actief;

    public FormulierDefinitie formulierDefinitie;

    public Map<String, List<String>> tabellen = new HashMap<>();

    public UUID zaakUuid;

    public UserEventListenerActie userEventListenerActie;

    public String toelichting;

    public LocalDate fataleDatum;

}
