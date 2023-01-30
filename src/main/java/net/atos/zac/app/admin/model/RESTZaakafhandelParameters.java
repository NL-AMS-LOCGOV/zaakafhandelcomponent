/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.model;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import net.atos.zac.app.zaken.model.RESTResultaattype;
import net.atos.zac.app.zaken.model.RESTZaakStatusmailOptie;

public class RESTZaakafhandelParameters {

    public Long id;

    public RESTZaaktypeOverzicht zaaktype;

    public RESTCaseDefinition caseDefinition;

    public String defaultBehandelaarId;

    public String defaultGroepId;

    public Integer einddatumGeplandWaarschuwing;

    public Integer uiterlijkeEinddatumAfdoeningWaarschuwing;

    public ZonedDateTime creatiedatum;

    public RESTResultaattype zaakNietOntvankelijkResultaattype;

    public RESTZaakStatusmailOptie intakeMail;

    public RESTZaakStatusmailOptie afrondenMail;

    public String productaanvraagtype;

    public String domein;

    public boolean valide;

    public List<RESTHumanTaskParameters> humanTaskParameters = new ArrayList<>();

    public List<RESTUserEventListenerParameter> userEventListenerParameters = new ArrayList<>();

    public List<RESTMailtemplateKoppeling> mailtemplateKoppelingen = new ArrayList<>();

    public List<RESTZaakbeeindigParameter> zaakbeeindigParameters = new ArrayList<>();

    public List<RESTZaakAfzender> zaakAfzenders = new ArrayList<>();

    public RESTZaakafhandelParameters() {
    }
}
