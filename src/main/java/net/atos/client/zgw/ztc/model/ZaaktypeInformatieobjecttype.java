/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.ztc.model;

import java.net.URI;
import java.util.UUID;

import javax.json.bind.annotation.JsonbTransient;

import net.atos.client.zgw.shared.util.URIUtil;

/**
 * ZAAKTYPE-INFORMATIEOBJECTTYPE relatie
 */
public class ZaaktypeInformatieobjecttype {

    /**
     * URL-referentie naar dit object. Dit is de unieke identificatie en locatie van dit object.
     */
    private URI url;

    /**
     * URL-referentie naar het ZAAKTYPE.
     */
    private URI zaaktype;

    /**
     * URL-referentie naar het INFORMATIEOBJECTTYPE.
     */
    private URI informatieobjecttype;


    /**
     * Uniek volgnummer van het ZAAK-INFORMATIEOBJECTTYPE binnen het ZAAKTYPE.
     */
    private Integer volgnummer;


    /**
     * Aanduiding van de richting van informatieobjecten van het gerelateerde INFORMATIEOBJECTTYPE bij zaken van het gerelateerde ZAAKTYPE.
     */
    private Richting richting;

    /**
     * URL-referentie naar het STATUSTYPE waarbij deze INFORMATIEOBJECTTYPEn verplicht aanwezig moeten zijn.
     */
    private URI statustype;


    public ZaaktypeInformatieobjecttype() {
    }

    public URI getUrl() {
        return url;
    }

    public void setUrl(final URI url) {
        this.url = url;
    }

    public URI getZaaktype() {
        return zaaktype;
    }

    public void setZaaktype(final URI zaaktype) {
        this.zaaktype = zaaktype;
    }

    public URI getInformatieobjecttype() {
        return informatieobjecttype;
    }

    public void setInformatieobjecttype(final URI informatieobjecttype) {
        this.informatieobjecttype = informatieobjecttype;
    }

    public Integer getVolgnummer() {
        return volgnummer;
    }

    public void setVolgnummer(final Integer volgnummer) {
        this.volgnummer = volgnummer;
    }

    public Richting getRichting() {
        return richting;
    }

    public void setRichting(final Richting richting) {
        this.richting = richting;
    }

    public URI getStatustype() {
        return statustype;
    }

    public void setStatustype(final URI statustype) {
        this.statustype = statustype;
    }

    @JsonbTransient
    public UUID getUUID() {
        return URIUtil.parseUUIDFromResourceURI(getUrl());
    }
}
