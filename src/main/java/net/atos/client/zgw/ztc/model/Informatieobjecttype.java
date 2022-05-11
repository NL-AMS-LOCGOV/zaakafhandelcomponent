/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.ztc.model;

import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;

import net.atos.client.zgw.shared.model.Vertrouwelijkheidaanduiding;
import net.atos.zac.util.UriUtil;

/**
 *
 */
public class Informatieobjecttype {

    public final static int OMSCHRIJVING_MAX = 80;

    /**
     * URL-referentie naar dit object. Dit is de unieke identificatie en locatie van dit object.
     */
    private URI url;

    /**
     * URL-referentie naar de CATALOGUS waartoe dit INFORMATIEOBJECTTYPE behoort.
     */
    private URI catalogus;

    /**
     * Omschrijving van de aard van informatieobjecten van dit INFORMATIEOBJECTTYPE.
     * maxLength: {@link Informatieobjecttype#OMSCHRIJVING_MAX}
     */
    private String omschrijving;

    /**
     * Aanduiding van de mate waarin informatieobjecten van dit INFORMATIEOBJECTTYPE voor de openbaarheid bestemd zijn.
     */
    private Vertrouwelijkheidaanduiding vertrouwelijkheidaanduiding;

    /**
     * De datum waarop het is ontstaan.
     */
    private LocalDate beginGeldigheid;

    /**
     * De datum waarop het is opgeheven.
     */
    private LocalDate eindeGeldigheid;

    /**
     * Geeft aan of het object een concept betreft.
     * Concepten zijn niet-definitieve versies en zouden niet gebruikt moeten worden buiten deze API.
     */
    private Boolean concept;

    /**
     * Constructor for PATCH request
     */
    public Informatieobjecttype() {
    }

    /**
     * Constructor with required attributes for POST and PUT requests
     */
    public Informatieobjecttype(final URI catalogus, final String omschrijving, final Vertrouwelijkheidaanduiding vertrouwelijkheidaanduiding,
            final LocalDate beginGeldigheid) {
        this.catalogus = catalogus;
        this.omschrijving = omschrijving;
        this.vertrouwelijkheidaanduiding = vertrouwelijkheidaanduiding;
        this.beginGeldigheid = beginGeldigheid;
    }

    /**
     * Constructor with readOnly attribures for GET response
     */
    @JsonbCreator
    public Informatieobjecttype(@JsonbProperty("url") final URI url,
            @JsonbProperty("concept") final Boolean concept) {
        this.url = url;
        this.concept = concept;
    }

    public URI getUrl() {
        return url;
    }

    public URI getCatalogus() {
        return catalogus;
    }

    public void setCatalogus(final URI catalogus) {
        this.catalogus = catalogus;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

    public void setOmschrijving(final String omschrijving) {
        this.omschrijving = omschrijving;
    }

    public Vertrouwelijkheidaanduiding getVertrouwelijkheidaanduiding() {
        return vertrouwelijkheidaanduiding;
    }

    public void setVertrouwelijkheidaanduiding(final Vertrouwelijkheidaanduiding vertrouwelijkheidaanduiding) {
        this.vertrouwelijkheidaanduiding = vertrouwelijkheidaanduiding;
    }

    public LocalDate getBeginGeldigheid() {
        return beginGeldigheid;
    }

    public void setBeginGeldigheid(final LocalDate beginGeldigheid) {
        this.beginGeldigheid = beginGeldigheid;
    }

    public LocalDate getEindeGeldigheid() {
        return eindeGeldigheid;
    }

    public void setEindeGeldigheid(final LocalDate eindeGeldigheid) {
        this.eindeGeldigheid = eindeGeldigheid;
    }

    public Boolean getConcept() {
        return concept;
    }

    @JsonbTransient
    public UUID getUUID() {
        return UriUtil.uuidFromURI(getUrl());
    }
}
