/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.planitems.model;

import java.util.Map;
import java.util.UUID;

import net.atos.zac.app.identity.model.RESTGroep;
import net.atos.zac.app.identity.model.RESTMedewerker;
import net.atos.zac.zaaksturing.model.FormulierDefinitie;

/**
 *
 */
public class RESTPlanItem {

    public String id;

    public String naam;

    public PlanItemType type;

    public RESTGroep groep;

    public RESTMedewerker medewerker;

    public FormulierDefinitie formulierDefinitie;

    public Map<String, String> taakdata;

    public UUID zaakUuid;

    public Boolean toelichtingVereist;

    public String toelichting;

    public RESTTaakStuurGegevens taakStuurGegevens;

    public String uitleg;
}
