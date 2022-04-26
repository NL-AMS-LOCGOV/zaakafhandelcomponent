/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.planitems.model;

import java.util.Map;

import net.atos.zac.app.identity.model.RESTGroep;
import net.atos.zac.app.identity.model.RESTMedewerker;

public class RESTHumanTaskData {

    public String planItemInstanceId;

    public RESTGroep groep;

    public RESTMedewerker medewerker;

    public Map<String, String> taakdata;

    public RESTTaakStuurGegevens taakStuurGegevens;
}
