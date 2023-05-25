/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.model;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import net.atos.client.zgw.shared.model.Vertrouwelijkheidaanduiding;
import net.atos.zac.app.admin.model.RESTZaakafhandelParameters;

public class RESTZaaktype {

    public UUID uuid;

    public String identificatie;

    public String doel;

    public String omschrijving;

    public String referentieproces;

    public boolean servicenorm;

    public LocalDate versiedatum;

    public LocalDate beginGeldigheid;

    public LocalDate eindeGeldigheid;

    public Vertrouwelijkheidaanduiding vertrouwelijkheidaanduiding;

    public boolean nuGeldig;

    public boolean opschortingMogelijk;

    public boolean verlengingMogelijk;

    public Integer verlengingstermijn;

    public List<RESTZaaktypeRelatie> zaaktypeRelaties;

    public List<UUID> informatieobjecttypes;

    public RESTZaakafhandelParameters zaakafhandelparameters;
}
