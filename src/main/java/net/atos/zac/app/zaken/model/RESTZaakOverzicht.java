/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.model;

import java.time.LocalDate;

import net.atos.zac.app.identity.model.RESTGroep;
import net.atos.zac.app.identity.model.RESTMedewerker;
import net.atos.zac.util.AbstractRESTObject;

public class RESTZaakOverzicht extends AbstractRESTObject {

    public String identificatie;

    public String toelichting;

    public String uuid;

    public LocalDate startdatum;

    public String zaaktype;

    public String status;

    public String aanvrager;

    public RESTMedewerker behandelaar;

    public LocalDate uiterlijkedatumafdoening;

    public RESTGroep groep;
}
