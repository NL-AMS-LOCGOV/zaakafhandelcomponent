/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.planitems.model;

import java.util.Map;

import net.atos.zac.app.identity.model.RESTGroep;
import net.atos.zac.zaaksturing.model.FormulierDefinition;

/**
 *
 */
public class RESTPlanItem {

    public String id;

    public String naam;

    public PlanItemType type;

    public RESTGroep groep;

    public FormulierDefinition formulierDefinitie;

    public Map<String, String> taakdata;
}
