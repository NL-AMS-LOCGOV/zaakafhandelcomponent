/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.model;

import java.util.List;

/**
 * 4 statustype; Intake, In behandeling, Heropend, Afgerond: met Afgerond als laatste statustypevolgnummer
 * min 1 resultaattype
 * Roltypen, omschrijving generiek: initiator en behandelaar. 1 overig roltype
 * Informatieobjecttype: e-mail
 * indien zaak besluit heeft, Besluittype
 */
public class RESTZaaktypeInrichtingscheck {

    public RESTZaaktypeOverzicht zaaktype;

    public boolean statustypeIntakeAanwezig;

    public boolean statustypeInBehandelingAanwezig;

    public boolean statustypeHeropendAanwezig;

    public boolean statustypeAfgerondAanwezig;

    public boolean statustypeAfgerondLaatsteVolgnummer;

    public boolean resultaattypeAanwezig;

    public boolean rolInitiatorAanwezig;

    public boolean rolBehandelaarAanwezig;

    public boolean rolOverigeAanwezig;

    public boolean informatieobjecttypeEmailAanwezig;

    public boolean besluittypeAanwezig;

    public List<String> resultaattypesMetVerplichtBesluit;

    public boolean zaakafhandelParametersValide;

    public boolean valide;
}
