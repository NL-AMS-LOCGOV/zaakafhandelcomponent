/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import static net.atos.client.zgw.shared.util.DateTimeUtil.DATE_TIME_FORMAT_WITH_MILLISECONDS;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.UUID;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbProperty;

/**
 *
 */
public class ZaakInformatieobject {

    public static final int TITEL_MAX_LENGTH = 200;

    /**
     * URL-referentie naar dit object.
     * Dit is de unieke identificatie en locatie van dit object.
     */
    private URI url;

    /**
     * Unieke resource identifier (UUID4)
     */
    private UUID uuid;

    /**
     * URL-referentie naar het INFORMATIEOBJECT (in de Documenten API), waar ook de relatieinformatie opgevraagd kan worden.
     */
    private URI informatieobject;

    /**
     * URL-referentie naar de ZAAK.
     */
    private URI zaak;

    /**
     * Aard relatie weergave
     */
    private AardRelatieWeergave aardRelatieWeergave;

    /**
     * De naam waaronder het INFORMATIEOBJECT binnen het OBJECT bekend is.
     * maxLength: {@link ZaakInformatieobject#TITEL_MAX_LENGTH}
     */
    private String titel;

    /**
     * Een op het object gerichte beschrijving van de inhoud vanhet INFORMATIEOBJECT.
     */
    private String beschrijving;

    /**
     * De datum waarop de behandelende organisatie het INFORMATIEOBJECT heeft geregistreerd bij het OBJECT.
     * Geldige waardes zijn datumtijden gelegen op of voor de huidige datum en tijd.
     */
    @JsonbDateFormat(DATE_TIME_FORMAT_WITH_MILLISECONDS)
    private ZonedDateTime registratiedatum;

    /**
     * Constructor for PATCH request
     */
    public ZaakInformatieobject() {
    }

    /**
     * Constructor with required attributes for POST and PUT requests
     */
    public ZaakInformatieobject(final URI informatieobject, final URI zaak) {
        this.informatieobject = informatieobject;
        this.zaak = zaak;
    }

    /**
     * Constructor with readOnly attributes for GET response
     */
    @JsonbCreator
    public ZaakInformatieobject(@JsonbProperty("url") final URI url,
            @JsonbProperty("uuid") final UUID uuid,
            @JsonbProperty("aardRelatieWeergave") final AardRelatieWeergave aardRelatieWeergave,
            @JsonbProperty("registratiedatum") final ZonedDateTime registratiedatum) {
        this.url = url;
        this.uuid = uuid;
        this.aardRelatieWeergave = aardRelatieWeergave;
        this.registratiedatum = registratiedatum;
    }

    public URI getUrl() {
        return url;
    }

    public UUID getUuid() {
        return uuid;
    }

    public URI getInformatieobject() {
        return informatieobject;
    }

    public void setInformatieobject(final URI informatieobject) {
        this.informatieobject = informatieobject;
    }

    public URI getZaak() {
        return zaak;
    }

    public void setZaak(final URI zaak) {
        this.zaak = zaak;
    }

    public AardRelatieWeergave getAardRelatieWeergave() {
        return aardRelatieWeergave;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(final String titel) {
        this.titel = titel;
    }

    public String getBeschrijving() {
        return beschrijving;
    }

    public void setBeschrijving(final String beschrijving) {
        this.beschrijving = beschrijving;
    }

    public ZonedDateTime getRegistratiedatum() {
        return registratiedatum;
    }
}
