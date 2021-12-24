/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.or.objecttype.model;

import java.net.URI;
import java.time.LocalDate;

/**
 *
 */
public class ObjecttypeVersion {

    /**
     * - readOnly
     */
    private URI url;

    /**
     * Integer version of the OBJECTTYPE
     * - readOnly
     */
    private Integer version;

    /**
     * - readOnly
     */
    private URI objectType;

    /**
     * Status of the object type version
     */
    private Status status;

    /**
     * JSON schema for Object validation
     */
    private String jsonSchema;

    /**
     * Date when the version was created
     * - readOnly
     */
    private LocalDate createdAt;

    /**
     * Last date when the version was modified
     * - readOnly
     */
    private LocalDate modifiedAt;

    /**
     * Date when the version was published
     * - readOnly
     */
    private LocalDate publishedAt;

    public URI getUrl() {
        return url;
    }

    public void setUrl(final URI url) {
        this.url = url;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(final Integer version) {
        this.version = version;
    }

    public URI getObjectType() {
        return objectType;
    }

    public void setObjectType(final URI objectType) {
        this.objectType = objectType;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    public String getJsonSchema() {
        return jsonSchema;
    }

    public void setJsonSchema(final String jsonSchema) {
        this.jsonSchema = jsonSchema;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(final LocalDate modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public LocalDate getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(final LocalDate publishedAt) {
        this.publishedAt = publishedAt;
    }
}
