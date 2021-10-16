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
public class Zaakeigenschap {

    /**
     * readOnly
     */
    private URI url;

    /**
     * Unieke resource identifier (UUID4)
     * <p>
     * readOnly
     */
    private UUID uuid;

    /**
     * required
     */
    private URI zaak;

    /**
     * URL-referentie naar de EIGENSCHAP (in de Catalogi API)
     * required
     */
    private URI eigenschap;

    /**
     * De naam van de EIGENSCHAP (overgenomen uit de Catalogi API).
     * readOnly
     */
    private String naam;

    /**
     * required
     */
    private String waarde;

    public Zaakeigenschap() {
    }

    public Zaakeigenschap(final URI zaak, final URI eigenschap, final String waarde) {
        this.zaak = zaak;
        this.eigenschap = eigenschap;
        this.waarde = waarde;
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

    public URI getEigenschap() {
        return eigenschap;
    }

    public void setEigenschap(final URI eigenschap) {
        this.eigenschap = eigenschap;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(final String naam) {
        this.naam = naam;
    }

    public String getWaarde() {
        return waarde;
    }

    public void setWaarde(final String waarde) {
        this.waarde = waarde;
    }
}
