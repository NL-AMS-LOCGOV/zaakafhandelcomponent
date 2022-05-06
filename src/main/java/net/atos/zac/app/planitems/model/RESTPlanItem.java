/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.planitems.model;

import java.util.UUID;

import net.atos.zac.app.identity.model.RESTGroup;
import net.atos.zac.zaaksturing.model.FormulierDefinitie;

/**
 *
 */
public class RESTPlanItem {

    public String id;

    public String naam;

    public PlanItemType type;

    public RESTGroup groep;

    public FormulierDefinitie formulierDefinitie;

    public UUID zaakUuid;

    public UserEventListenerActie userEventListenerActie;

    public String toelichting;
}
