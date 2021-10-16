/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import java.net.URI;

import javax.ws.rs.QueryParam;

import net.atos.client.zgw.shared.model.AbstractListParameters;

/**
 *
 */
public class ZaakinformatieobjectListParameters extends AbstractListParameters {

    /**
     * URL-referentie naar de ZAAK.
     */
    @QueryParam("zaak")
    private URI zaak;

    /**
     * URL-referentie naar het INFORMATIEOBJECT (in de Documenten API), waar ook de relatieinformatie opgevraagd kan worden.
     */
    @QueryParam("informatieobject")
    private URI informatieobject;

    public URI getZaak() {
        return zaak;
    }

    public void setZaak(final URI zaak) {
        this.zaak = zaak;
    }

    public URI getInformatieobject() {
        return informatieobject;
    }

    public void setInformatieobject(final URI informatieobject) {
        this.informatieobject = informatieobject;
    }
}
