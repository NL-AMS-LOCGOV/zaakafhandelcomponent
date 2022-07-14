/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.model;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import net.atos.zac.app.identity.model.RESTGroup;
import net.atos.zac.app.identity.model.RESTUser;
import net.atos.zac.app.zaken.model.RESTZaaktype;

public class RESTZaakafhandelParameters {

    public Long id;

    public RESTZaaktype zaaktype;

    public RESTCaseDefinition caseDefinition;

    public RESTUser defaultBehandelaar;

    public RESTGroup defaultGroep;

    public Integer einddatumGeplandWaarschuwing;

    public Integer uiterlijkeEinddatumAfdoeningWaarschuwing;

    public ZonedDateTime creatiedatum;

    public RESTZaakResultaattype zaakNietOntvankelijkResultaat;

    public boolean valide;

    public List<RESTHumanTaskParameters> humanTaskParameters = new ArrayList<>();

    public List<RESTUserEventListenerParameter> userEventListenerParameters = new ArrayList<>();

    public List<RESTZaakbeeindigParameter> zaakbeeindigParameters = new ArrayList<>();

    public RESTZaakafhandelParameters() {
    }
}
