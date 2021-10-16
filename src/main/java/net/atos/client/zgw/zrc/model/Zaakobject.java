/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import java.net.URI;
import java.util.UUID;

/**
 *
 */
public class Zaakobject {

    /**
     * URL-referentie naar dit object. Dit is de unieke identificatie en locatie van dit object
     * - readOnly
     */
    private URI url;

    /**
     * Unieke resource identifier (UUID4)
     * - readOnly
     */
    private UUID uuid;

    /**
     * URL-referentie naar de ZAAK
     * - required
     */
    private URI zaak;

    /**
     * URL-referentie naar de resource die het OBJECT beschrijft
     */
    private URI object;

    /**
     * Beschrijft het type OBJECT gerelateerd aan de ZAAK
     * - required
     */
    private Objecttype objectType;

    /**
     * Beschrijft het type OBJECT als `objectType` de waarde "overige" heeft
     * - maxLength: 100
     * - pattern: '[a-z\_]+'
     */
    private String objectTypeOverige;

    /**
     * Omschrijving van de betrekking tussen de ZAAK en het OBJECT
     * - maxLength: 80
     */
    private String relatieomschrijving;

    public Zaakobject() {
    }

    /**
     * Constructor with required attributes
     */
    public Zaakobject(final URI zaak, final Objecttype objectType) {
        this.zaak = zaak;
        this.objectType = objectType;
    }

    public URI getUrl() {
        return url;
    }

    public void setUrl(final URI url) {
        this.url = url;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(final UUID uuid) {
        this.uuid = uuid;
    }

    public URI getZaak() {
        return zaak;
    }

    public void setZaak(final URI zaak) {
        this.zaak = zaak;
    }

    public URI getObject() {
        return object;
    }

    public void setObject(final URI object) {
        this.object = object;
    }

    public Objecttype getObjectType() {
        return objectType;
    }

    public void setObjectType(final Objecttype objectType) {
        this.objectType = objectType;
    }

    public String getObjectTypeOverige() {
        return objectTypeOverige;
    }

    public void setObjectTypeOverige(final String objectTypeOverige) {
        this.objectTypeOverige = objectTypeOverige;
    }

    public String getRelatieomschrijving() {
        return relatieomschrijving;
    }

    public void setRelatieomschrijving(final String relatieomschrijving) {
        this.relatieomschrijving = relatieomschrijving;
    }
}
