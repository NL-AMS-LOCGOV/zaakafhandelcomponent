/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken.model;

import java.time.LocalDate;
import java.util.List;

import net.atos.zac.app.policy.model.RESTTaakRechten;
import net.atos.zac.app.taken.model.TaakStatus;

public class RESTTaakZoekObject extends AbstractRESTZoekObject {

    public String naam;

    public String toelichting;

    public TaakStatus status;

    public String zaakUuid;

    public String zaakIdentificatie;

    public String zaakOmschrijving;

    public String zaakToelichting;

    public String zaaktypeUuid;

    public String zaaktypeIdentificatie;

    public String zaaktypeOmschrijving;

    public LocalDate creatiedatum;

    public LocalDate toekenningsdatum;

    public LocalDate fataledatum;

    public String groepID;

    public String groepNaam;

    public String behandelaarNaam;

    public String behandelaarGebruikersnaam;

    public List<String> taakData;

    public List<String> taakInformatie;

    public RESTTaakRechten rechten;

}
