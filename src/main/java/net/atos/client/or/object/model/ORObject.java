/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.or.object.model;

import java.net.URI;
import java.util.UUID;

/**
 * Object registratie obkject.
 */
public class ORObject {

    /**
     * URL-referentie naar dit object. Dit is de unieke identificatie en locatie van dit object.
     * - required
     * - readOnly
     */
    private URI url;

    /**
     * Unique identifier (UUID4)
     */
    private UUID uuid;

    /**
     * Url reference to OBJECTTYPE in Objecttypes API
     * - required
     */
    private URI type;

    /**
     * required
     */
    private ObjectRecord record;

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

    public URI getType() {
        return type;
    }

    public void setType(final URI type) {
        this.type = type;
    }

    public ObjectRecord getRecord() {
        return record;
    }

    public void setRecord(final ObjectRecord record) {
        this.record = record;
    }
}
