/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.model;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import net.atos.client.zgw.brc.model.Vervalreden;
import net.atos.zac.app.informatieobjecten.model.RESTEnkelvoudigInformatieobject;

public class RESTBesluit {

    public URI url;

    public UUID uuid;

    public String identificatie;

    public LocalDate datum;

    public RESTBesluittype besluittype;

    public LocalDate ingangsdatum;

    public LocalDate vervaldatum;

    public Vervalreden vervalreden;

    public String toelichting;

    public UUID zaakUuid;

    public List<RESTEnkelvoudigInformatieobject> informatieobjecten;

}
