/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.model;

import java.time.LocalDate;

import net.atos.zac.app.identity.model.RESTGroup;
import net.atos.zac.app.identity.model.RESTUser;

public class RESTZaakOverzicht {

    public String identificatie;

    public String toelichting;

    public String omschrijving;

    public String uuid;

    public LocalDate startdatum;

    public LocalDate einddatum;

    public String zaaktype;

    public String status;

    public RESTUser behandelaar;

    public LocalDate einddatumGepland;

    public LocalDate uiterlijkeEinddatumAfdoening;

    public RESTGroup groep;

    public RESTZaakResultaat resultaat;

    public RESTOpenstaandeTaken openstaandeTaken;
}
