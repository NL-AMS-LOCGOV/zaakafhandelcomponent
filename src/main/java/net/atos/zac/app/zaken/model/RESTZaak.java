/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.model;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import net.atos.zac.app.identity.model.RESTGroup;
import net.atos.zac.app.identity.model.RESTUser;
import net.atos.zac.app.klanten.model.IdentificatieType;

public class RESTZaak {

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

    public LocalDate archiefActiedatum;

    public String archiefNominatie;

    public RESTCommunicatiekanaal communicatiekanaal;

    public String vertrouwelijkheidaanduiding;

    public RESTGeometry zaakgeometrie;

    public boolean isOpgeschort;

    public String redenOpschorting;

    public boolean isVerlengd;

    public String redenVerlenging;

    public String duurVerlenging;

    public RESTGroup groep;

    public RESTUser behandelaar;

    public List<RESTGerelateerdeZaak> gerelateerdeZaken;

    public List<RESTZaakKenmerk> kenmerken;

    public List<RESTZaakEigenschap> eigenschappen;

    public IdentificatieType initiatorIdentificatieType;

    public String initiatorIdentificatie;

    public boolean isHeropend;

    public boolean isHoofdzaak;

    public boolean isDeelzaak;

    public RESTZaakActies acties;
}
