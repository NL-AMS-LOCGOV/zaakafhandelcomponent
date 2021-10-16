/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.drc.model;

import static net.atos.client.zgw.shared.util.DateTimeUtil.DATE_TIME_FORMAT;

import java.net.URI;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbProperty;

/**
 *
 */
public class Gebruiksrechten {

    /**
     * URL-referentie naar dit object. Dit is de unieke identificatie en locatie van dit object.
     */
    private URI url;

    /**
     * URL-referentie naar het INFORMATIEOBJECT.
     */
    private URI informatieobject;

    /**
     * Begindatum van de periode waarin de gebruiksrechtvoorwaarden van toepassing zijn.
     * Doorgaans is de datum van creatie van het informatieobject de startdatum.
     */
    @JsonbDateFormat(value = DATE_TIME_FORMAT)
    private ZonedDateTime startdatum;

    /**
     * Einddatum van de periode waarin de gebruiksrechtvoorwaarden van toepassing zijn.
     */
    private LocalDate einddatum;

    /**
     * Omschrijving van de van toepassing zijnde voorwaarden aan het gebruik anders dan raadpleging
     */
    private String omschrijvingVoorwaarden;

    /**
     * Constructor for PATCH request
     */
    public Gebruiksrechten() {
    }

    /**
     * Constructor with required attributes for POST and PUT request
     */
    public Gebruiksrechten(final URI informatieobject, final ZonedDateTime startdatum, final String omschrijvingVoorwaarden) {
        this.informatieobject = informatieobject;
        this.startdatum = startdatum;
        this.omschrijvingVoorwaarden = omschrijvingVoorwaarden;
    }

    /**
     * Constructor with readOnly attributes for GET response
     *
     * @param url
     */
    @JsonbCreator
    public Gebruiksrechten(@JsonbProperty("url") final URI url) {
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

    public ZonedDateTime getStartdatum() {
        return startdatum;
    }

    public void setStartdatum(final ZonedDateTime startdatum) {
        this.startdatum = startdatum;
    }

    public LocalDate getEinddatum() {
        return einddatum;
    }

    public void setEinddatum(final LocalDate einddatum) {
        this.einddatum = einddatum;
    }

    public String getOmschrijvingVoorwaarden() {
        return omschrijvingVoorwaarden;
    }

    public void setOmschrijvingVoorwaarden(final String omschrijvingVoorwaarden) {
        this.omschrijvingVoorwaarden = omschrijvingVoorwaarden;
    }
}
