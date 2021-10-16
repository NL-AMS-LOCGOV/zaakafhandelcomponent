/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.or.objecttype.model;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 *
 */
public class Objecttype {

    /**
     * URL-referentie naar dit object. Dit is de unieke identificatie en locatie van dit object.
     * - readOnly
     */
    private URI url;

    /**
     * Unique identifier (UUID4)
     */
    private UUID uuid;

    /**
     * Name of the object type
     * - required
     * - maxLength: 100
     */
    private String name;

    /**
     * Plural name of the object type
     * - required
     * - maxLength: 100
     */
    private String namePlural;

    /**
     * The description of the object type
     * - maxLength: 1000
     */
    private String description;

    /**
     * Confidential level of the object type
     */
    private Dataclassification dataClassification;

    /**
     * Organization which is responsible for the object type
     * - maxLength: 200
     */
    private String maintainerOrganization;

    /**
     * Business department which is responsible for the object type
     * - maxLength: 200
     */
    private String maintainerDepartment;

    /**
     * Name of the person in the organization who can provide information about the object type
     * - maxLength: 200
     */
    private String contactPerson;

    /**
     * Email of the person in the organization who can provide information about the object type
     * - maxLength: 200
     */
    private String contactEmail;

    /**
     * Name of the system from which the object type originates
     * - maxLength: 200
     */
    private String source;

    /**
     * Indicates how often the object type is updated
     */
    private Updatefrequency updateFrequency;

    /**
     * Organization which is responsible for publication of the object type
     * - maxLength: 200
     */
    private String providerOrganization;

    /**
     * Link to the documentation for the object type
     */
    private URI documentationUrl;

    /**
     * Key-value pairs of keywords related for the object type
     */
    // ToDo: labels

    /**
     * Date when the object type was created
     * - readOnly
     */
    private LocalDate createdAt;

    /**
     * Last date when the object type was modified
     * - readOnly
     */
    private LocalDate modifiedAt;

    /**
     * List of URLs for the OBJECTTYPE versions
     * - readOnly
     * - uniqueItems: true
     */
    private List<URI> versions;

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

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getNamePlural() {
        return namePlural;
    }

    public void setNamePlural(final String namePlural) {
        this.namePlural = namePlural;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Dataclassification getDataClassification() {
        return dataClassification;
    }

    public void setDataClassification(final Dataclassification dataClassification) {
        this.dataClassification = dataClassification;
    }

    public String getMaintainerOrganization() {
        return maintainerOrganization;
    }

    public void setMaintainerOrganization(final String maintainerOrganization) {
        this.maintainerOrganization = maintainerOrganization;
    }

    public String getMaintainerDepartment() {
        return maintainerDepartment;
    }

    public void setMaintainerDepartment(final String maintainerDepartment) {
        this.maintainerDepartment = maintainerDepartment;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(final String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(final String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getSource() {
        return source;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    public Updatefrequency getUpdateFrequency() {
        return updateFrequency;
    }

    public void setUpdateFrequency(final Updatefrequency updateFrequency) {
        this.updateFrequency = updateFrequency;
    }

    public String getProviderOrganization() {
        return providerOrganization;
    }

    public void setProviderOrganization(final String providerOrganization) {
        this.providerOrganization = providerOrganization;
    }

    public URI getDocumentationUrl() {
        return documentationUrl;
    }

    public void setDocumentationUrl(final URI documentationUrl) {
        this.documentationUrl = documentationUrl;
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

    public List<URI> getVersions() {
        return versions;
    }

    public void setVersions(final List<URI> versions) {
        this.versions = versions;
    }
}
