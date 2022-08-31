/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.ztc.model;

import java.net.URI;
import java.util.List;

import javax.ws.rs.QueryParam;

import net.atos.client.zgw.shared.model.AbstractListParameters;

/**
 * ListParameters for Besluittype
 */
public class BesluittypeListParameters extends AbstractListParameters {

    /**
     * URL-referentie naar de CATALOGUS waartoe dit BESLUITTYPE behoort.
     */
    @QueryParam("catalogus")
    private URI catalogus;

    /**
     * URL-referentie naar het ZAAKTYPE van ZAAKen waarin resultaten van dit RESULTAATTYPE bereikt kunnen worden.
     */
    @QueryParam("zaaktypen")
    private List<URI> zaaktypen;

    /**
     * Het INFORMATIEOBJECTTYPE van informatieobjecten waarin besluiten van dit BESLUITTYPE worden vastgelegd.
     */
    @QueryParam("informatieobjecttypen")
    private List<URI> informatieobjecttypen;

    /*
     * Filter objects depending on their concept status
     */
    private ObjectStatusFilter status;


    public BesluittypeListParameters() {
    }

    public BesluittypeListParameters(URI zaaktype) {
        this.zaaktypen = List.of(zaaktype);
    }
}
