/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.informatieobjecten.model;

import java.net.URI;

import net.atos.client.zgw.shared.model.Vertrouwelijkheidaanduiding;

public class RESTInformatieobjecttype {
    public URI url;

    public String omschrijving;

    public Vertrouwelijkheidaanduiding vertrouwelijkheidaanduiding;

    public boolean concept;
}
