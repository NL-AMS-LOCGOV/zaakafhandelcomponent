/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model.zaakobjecten;

import java.net.URI;
import java.util.UUID;

import javax.json.bind.annotation.JsonbTransient;
import javax.json.bind.annotation.JsonbTypeDeserializer;

import org.apache.commons.lang3.builder.EqualsBuilder;

import net.atos.client.zgw.zrc.model.Objecttype;
import net.atos.client.zgw.zrc.util.ZaakObjectJsonbDeserializer;

/**
 * Zaakobject
 */
@JsonbTypeDeserializer(ZaakObjectJsonbDeserializer.class)
public abstract class Zaakobject {

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

    /**
     * Constructor for JSONB deserialization
     */
    public Zaakobject() {
    }

    /**
     * Constructor with required attributes
     */
    public Zaakobject(final URI zaakUri, final URI objectUri, final Objecttype objectType) {
        this.zaak = zaakUri;
        this.object = objectUri;
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Zaakobject that = (Zaakobject) o;
        return new EqualsBuilder().append(zaak, that.zaak).append(object, that.object).append(objectType, that.objectType)
                .append(objectTypeOverige, that.objectTypeOverige).isEquals();
    }

    @Override
    public int hashCode() {
        int result = zaak != null ? zaak.hashCode() : 0;
        result = 31 * result + (object != null ? object.hashCode() : 0);
        result = 31 * result + (objectType != null ? objectType.hashCode() : 0);
        result = 31 * result + (objectTypeOverige != null ? objectTypeOverige.hashCode() : 0);
        return result;
    }

    @JsonbTransient
    public boolean isBagObject() {
        return switch (objectType) {
            case ADRES, PAND, OPENBARE_RUIMTE, WOONPLAATS -> true;
            case OVERIGE -> ZaakobjectNummeraanduiding.OBJECT_TYPE_OVERIGE.equals(objectTypeOverige);
            default -> false;
        };
    }
}
