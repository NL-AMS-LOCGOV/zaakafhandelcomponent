/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import static net.atos.client.zgw.shared.util.DateTimeUtil.DATE_TIME_FORMAT;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.UUID;

import javax.json.bind.annotation.JsonbDateFormat;

/**
 * KLANTCONTACT
 */
public class KlantContact {

    /**
     * URL-referentie naar dit object. Dit is de unieke identificatie en locatie van dit object.
     */
    private URI url;

    /**
     * Unieke resource identifier (UUID4)
     */
    private UUID uuid;

    /**
     * URL-referentie naar de ZAAK.
     * required
     */
    private URI zaak;

    /**
     * De unieke aanduiding van een KLANTCONTACT
     * required
     */
    private String identificatie;

    /**
     * De datum en het tijdstip waarop het KLANTCONTACT begint
     * required
     */
    @JsonbDateFormat(value = DATE_TIME_FORMAT)
    private ZonedDateTime datumtijd;

    /**
     * Het communicatiekanaal waarlangs het KLANTCONTACT gevoerd wordt
     */
    private String kanaal;

    /**
     * Het onderwerp waarover contact is geweest met de klant.
     */
    private String onderwerp;

    /**
     * Een toelichting die inhoudelijk het contact met de klant beschrijft.
     */
    private String toelichting;

    public KlantContact() {
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

    public String getIdentificatie() {
        return identificatie;
    }

    public void setIdentificatie(final String identificatie) {
        this.identificatie = identificatie;
    }

    public String getKanaal() {
        return kanaal;
    }

    public void setKanaal(final String kanaal) {
        this.kanaal = kanaal;
    }

    public String getOnderwerp() {
        return onderwerp;
    }

    public void setOnderwerp(final String onderwerp) {
        this.onderwerp = onderwerp;
    }

    public String getToelichting() {
        return toelichting;
    }

    public void setToelichting(final String toelichting) {
        this.toelichting = toelichting;
    }
}
