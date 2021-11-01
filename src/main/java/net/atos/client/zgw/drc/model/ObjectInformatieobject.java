/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.drc.model;

import java.net.URI;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

/**
 *
 */
public class ObjectInformatieobject {

    /**
     * URL-referentie naar dit object. Dit is de unieke identificatie en locatie van dit object.
     */
    private URI url;

    /**
     * URL-referentie naar het INFORMATIEOBJECT.
     */
    private URI informatieobject;

    /**
     * URL-referentie naar het gerelateerde OBJECT (in deze of een andere API).
     */
    private URI object;

    /**
     * Het type van het gerelateerde OBJECT.
     */
    private ObjectType objectType;

    /**
     * Constructor for PATCH request
     */
    public ObjectInformatieobject() {
    }

    /**
     * Constructor with required attributes for POST and PUT request
     */
    public ObjectInformatieobject(final URI informatieobject, final URI object, final ObjectType objectType) {
        this.informatieobject = informatieobject;
        this.object = object;
        this.objectType = objectType;
    }

    /**
     * Constructor with readOnly attributes for GET response
     */
    @JsonbCreator
    public ObjectInformatieobject(@JsonbProperty("url") final URI url) {
        this.url = url;
    }

    public URI getUrl() {
        return url;
    }

    public URI getInformatieobject() {
        return informatieobject;
    }

    public void setInformatieobject(final URI informatieobject) {
        this.informatieobject = informatieobject;
    }

    public URI getObject() {
        return object;
    }

    public void setObject(URI object) {
        this.object = object;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }
}
