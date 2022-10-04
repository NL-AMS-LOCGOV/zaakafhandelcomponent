/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.ztc.model;

import static net.atos.zac.configuratie.ConfiguratieService.STATUSTYPE_OMSCHRIJVING_HEROPEND;

import java.net.URI;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;

/**
 *
 */
public class Statustype {

    public static final int OMSCHRIJVING_MAX_LENGTH = 80;

    public static final int STATUSTEKST_MAX_LENGTH = 80;

    /**
     * URL-referentie naar dit object.
     * Dit is de unieke identificatie en locatie van dit object.
     */
    private URI url;

    /**
     * Een korte, voor de initiator van de zaak relevante, omschrijving van de aard van de STATUS van zaken van een ZAAKTYPE
     * - maxLength: {@link Statustype#OMSCHRIJVING_MAX_LENGTH}
     */
    private String omschrijving;

    /**
     * Algemeen gehanteerde omschrijving van de aard van STATUSsen van het STATUSTYPE
     */
    private String omschrijvingGeneriek;

    /**
     * De tekst die wordt gebruikt om de Initiator te informeren over het bereiken van een STATUS van dit STATUSTYPE bij het desbetreffende ZAAKTYPE.
     * - maxLength: {@link Statustype#OMSCHRIJVING_MAX_LENGTH}
     */
    private String statustekst;

    /**
     * URL-referentie naar het ZAAKTYPE van ZAAKen waarin STATUSsen van dit STATUSTYPE bereikt kunnen worden.
     */
    private URI zaaktype;

    /**
     * Een volgnummer voor statussen van het STATUSTYPE binnen een zaak.
     * Start met 1.
     */
    private Integer volgnummer;

    /**
     * Geeft aan dat dit STATUSTYPE een eindstatus betreft.
     * Dit gegeven is afgeleid uit alle STATUSTYPEn van dit ZAAKTYPE met het hoogste volgnummer.
     */
    private Boolean isEindstatus;

    /**
     * Aanduiding die aangeeft of na het zetten van een STATUS van dit STATUSTYPE de Initiator moet worden geinformeerd over de statusovergang
     */
    private Boolean informeren;


    /**
     * Een eventuele toelichting op dit STATUSTYPE.
     */
    private String toelichting;

    /**
     * Constructor for PATCH request
     */
    public Statustype() {
    }

    /**
     * Constructor with required attributes for POST and PUT requests
     */
    public Statustype(final String omschrijving, final URI zaaktype, final Integer volgnummer) {
        this.omschrijving = omschrijving;
        this.zaaktype = zaaktype;
        this.volgnummer = volgnummer;
    }

    /**
     * Constructor with readOnly attributes for GET response
     */
    @JsonbCreator
    public Statustype(@JsonbProperty("url") final URI url,
            @JsonbProperty("isEindstatus") final Boolean isEindstatus) {
        this.url = url;
        this.isEindstatus = isEindstatus;
    }

    public URI getUrl() {
        return url;
    }

    public Boolean getEindstatus() {
        return isEindstatus;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

    public void setOmschrijving(final String omschrijving) {
        this.omschrijving = omschrijving;
    }

    public String getOmschrijvingGeneriek() {
        return omschrijvingGeneriek;
    }

    public void setOmschrijvingGeneriek(final String omschrijvingGeneriek) {
        this.omschrijvingGeneriek = omschrijvingGeneriek;
    }

    public String getStatustekst() {
        return statustekst;
    }

    public void setStatustekst(final String statustekst) {
        this.statustekst = statustekst;
    }

    public URI getZaaktype() {
        return zaaktype;
    }

    public void setZaaktype(final URI zaaktype) {
        this.zaaktype = zaaktype;
    }

    public Integer getVolgnummer() {
        return volgnummer;
    }

    public void setVolgnummer(final Integer volgnummer) {
        this.volgnummer = volgnummer;
    }

    public Boolean getInformeren() {
        return informeren;
    }

    public void setInformeren(final Boolean informeren) {
        this.informeren = informeren;
    }

    public String getToelichting() {
        return toelichting;
    }

    public void setToelichting(String toelichting) {
        this.toelichting = toelichting;
    }

    @JsonbTransient
    public static boolean isHeropend(final Statustype statustype) {
        return statustype != null && STATUSTYPE_OMSCHRIJVING_HEROPEND.equals(statustype.getOmschrijving());
    }
}
