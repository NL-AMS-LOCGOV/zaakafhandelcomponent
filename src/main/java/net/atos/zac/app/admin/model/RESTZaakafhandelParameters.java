/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.model;

import java.util.ArrayList;
import java.util.List;

import net.atos.zac.app.identity.model.RESTGroep;
import net.atos.zac.app.identity.model.RESTMedewerker;
import net.atos.zac.app.zaken.model.RESTZaaktype;

public class RESTZaakafhandelParameters {
    public RESTZaaktype zaaktype;

    public RESTCaseDefinition caseDefinition;

    public RESTMedewerker defaultBehandelaar;

    public RESTGroep defaultGroep;

    public List<RESTPlanItemParameters> planItemParameters = new ArrayList<>();

    public RESTZaakafhandelParameters() {
    }
}
