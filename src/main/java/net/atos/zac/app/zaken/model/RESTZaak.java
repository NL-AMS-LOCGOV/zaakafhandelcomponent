/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.model;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import net.atos.zac.app.identity.model.RESTGroep;
import net.atos.zac.app.identity.model.RESTMedewerker;
import net.atos.zac.util.AbstractRESTObject;

public class RESTZaak extends AbstractRESTObject {
    public UUID uuid;

    public String identificatie;

    public String omschrijving;

    public String toelichting;

    public RESTZaaktype zaaktype;

    public RESTZaakStatus status;

    public RESTZaakResultaat resultaat;

    public String bronorganisatie;

    public String verantwoordelijkeOrganisatie;

    public LocalDate registratiedatum;

    public LocalDate startdatum;

    public LocalDate einddatumGepland;

    public LocalDate einddatum;

    public LocalDate uiterlijkeEinddatumAfdoening;

    public LocalDate publicatiedatum;

    public String communicatiekanaal;

    public String vertrouwelijkheidaanduiding;

    public String zaakgeometrie;

    public boolean indicatieOpschorting;

    public String redenOpschorting;

    public boolean indicatieVerlenging;

    public String redenVerlenging;

    public String duurVerlenging;

    public RESTGroep groep;

    public RESTMedewerker behandelaar;

    public List<RESTGerelateerdeZaak> gerelateerdeZaken;

    public List<RESTZaakKenmerk> kenmerken;

    public List<RESTZaakEigenschap> eigenschappen;

}
