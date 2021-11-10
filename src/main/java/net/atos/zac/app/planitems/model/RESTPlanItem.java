/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.planitems.model;

import java.util.Map;

import net.atos.zac.app.identity.model.RESTGroep;

/**
 *
 */
public class RESTPlanItem {

    public String id;

    public String naam;

    public PlanItemType type;

    public RESTGroep groep;

    public String taakStartFormulier;

    public Map<String, String> taakdata;
}
