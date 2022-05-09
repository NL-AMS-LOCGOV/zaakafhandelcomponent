/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.planitems.model;

import java.util.Map;

import net.atos.zac.app.identity.model.RESTGroup;
import net.atos.zac.app.identity.model.RESTUser;

public class RESTHumanTaskData {

    public String planItemInstanceId;

    public RESTGroup groep;

    public RESTUser medewerker;

    public Map<String, String> taakdata;

    public RESTTaakStuurGegevens taakStuurGegevens;
}
