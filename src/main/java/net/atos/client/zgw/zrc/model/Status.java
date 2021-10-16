/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import static net.atos.client.zgw.shared.util.DateTimeUtil.DATE_TIME_FORMAT;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.UUID;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbProperty;

/**
 *
 */
public class Status {

    private static final int STATUSTOELICHTING_MAX_LENGTH = 1000;

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
     * URL-referentie naar de ZAAK.
     */
    private final URI zaak;

    /**
     * URL-referentie naar het STATUSTYPE (in de Catalogi API).
     */
    private final URI statustype;

    /**
     * De datum waarop de ZAAK de status heeft verkregen.
     */
    @JsonbDateFormat(value = DATE_TIME_FORMAT)
    private final ZonedDateTime datumStatusGezet;

    /**
     * Een, voor de initiator van de zaak relevante, toelichting op de status van een zaak.
     * maxLength: {@link Status#STATUSTOELICHTING_MAX_LENGTH}
     */
    private String statustoelichting;

    /**
     * Constructor with required attributes for POST request
     */
    public Status(final URI zaak, final URI statustype, final ZonedDateTime datumStatusGezet) {
        this.zaak = zaak;
        this.statustype = statustype;
        this.datumStatusGezet = datumStatusGezet;
    }

    /**
     * Constructor with required and readOnly attributes for GET request
     */
    @JsonbCreator
    public Status(@JsonbProperty("url") final URI url,
            @JsonbProperty("uuid") final UUID uuid,
            @JsonbProperty("zaak") final URI zaak,
            @JsonbProperty("statustype") final URI statustype,
            @JsonbProperty("datumStatusGezet") final ZonedDateTime datumStatusGezet) {
        this.url = url;
        this.uuid = uuid;
        this.zaak = zaak;
        this.statustype = statustype;
        this.datumStatusGezet = datumStatusGezet;
    }


    public URI getUrl() {
        return url;
    }

    public UUID getUuid() {
        return uuid;
    }

    public URI getZaak() {
        return zaak;
    }

    public URI getStatustype() {
        return statustype;
    }

    public ZonedDateTime getDatumStatusGezet() {
        return datumStatusGezet;
    }

    public String getStatustoelichting() {
        return statustoelichting;
    }

    public void setStatustoelichting(final String statustoelichting) {
        this.statustoelichting = statustoelichting;
    }
}
