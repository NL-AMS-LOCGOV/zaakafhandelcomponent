/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.model.audit;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.UUID;

import net.atos.client.zgw.shared.model.Bron;

/**
 * Gegevens mbt een wijziging gedaan op een object
 */

public class AuditTrailRegel {

    /**
     * URL-referentie naar dit object. Dit is de unieke identificatie en locatie van dit object.
     */
    private URI url;

    /**
     * Unieke resource identifier (UUID4)
     */
    private UUID uuid;

    /**
     * De naam van het component waar de wijziging in is gedaan.
     */
    private Bron bron;

    /**
     * Unieke identificatie van de applicatie, binnen de organisatie.
     * maxLength: 100
     */
    private String applicatieId;

    /**
     * Vriendelijke naam van de applicatie.
     * maxLength: 200
     */
    private String applicatieWeergave;

    /**
     * Unieke identificatie van de gebruiker die binnen de organisatie herleid kan worden naar een persoon.
     * maxLenght: 255
     */
    private String gebruikersId;

    /**
     * Vriendelijke naam van de gebruiker.
     * maxLenght: 255
     */
    private String gebruikersWeergave;

    /**
     * De uitgevoerde handeling.
     * maxLength: 50
     * <p>
     * De bekende waardes voor dit veld zijn hieronder aangegeven, maar andere waardes zijn ook toegestaan
     * <p>
     * Uitleg bij mogelijke waarden:
     * create - Object aangemaakt
     * list - Lijst van objecten opgehaald
     * retrieve - Object opgehaald
     * destroy - Object verwijderd
     * update - Object bijgewerkt
     * partial_update - Object deels bijgewerkt
     */
    private String actie;

    /**
     * Vriendelijke naam van de actie.
     * maxLength: 200
     */
    private String actieWeergave;

    /**
     * HTTP status code van de API response van de uitgevoerde handeling.
     * min: 100
     * max: 599
     */
    private int resultaat;

    /**
     * De URL naar het hoofdobject van een component.
     */
    private URI hoofdObject;

    /**
     * Het type resource waarop de actie gebeurde.
     * maxLength: 50
     */
    private String resource;

    /**
     * De URL naar het object.
     */
    private URI resourceUrl;

    /**
     * Toelichting waarom de handeling is uitgevoerd.
     */
    private String toelichting;

    /**
     * Vriendelijke identificatie van het object.
     * maxLength: 200
     */
    private String resourceWeergave;

    /**
     * De datum waarop de handeling is gedaan.
     */
    private ZonedDateTime aanmaakdatum;

    /**
     * object (Wijzigingen) oud en nieuw
     */
    private AuditWijziging<?> wijzigingen;


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

    public Bron getBron() {
        return bron;
    }

    public void setBron(final Bron bron) {
        this.bron = bron;
    }

    public String getApplicatieId() {
        return applicatieId;
    }

    public void setApplicatieId(final String applicatieId) {
        this.applicatieId = applicatieId;
    }

    public String getApplicatieWeergave() {
        return applicatieWeergave;
    }

    public void setApplicatieWeergave(final String applicatieWeergave) {
        this.applicatieWeergave = applicatieWeergave;
    }

    public String getGebruikersId() {
        return gebruikersId;
    }

    public void setGebruikersId(final String gebruikersId) {
        this.gebruikersId = gebruikersId;
    }

    public String getGebruikersWeergave() {
        return gebruikersWeergave;
    }

    public void setGebruikersWeergave(final String gebruikersWeergave) {
        this.gebruikersWeergave = gebruikersWeergave;
    }

    public String getActie() {
        return actie;
    }

    public void setActie(final String actie) {
        this.actie = actie;
    }

    public String getActieWeergave() {
        return actieWeergave;
    }

    public void setActieWeergave(final String actieWeergave) {
        this.actieWeergave = actieWeergave;
    }

    public int getResultaat() {
        return resultaat;
    }

    public void setResultaat(final int resultaat) {
        this.resultaat = resultaat;
    }

    public URI getHoofdObject() {
        return hoofdObject;
    }

    public void setHoofdObject(final URI hoofdObject) {
        this.hoofdObject = hoofdObject;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(final String resource) {
        this.resource = resource;
    }

    public URI getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(final URI resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public String getToelichting() {
        return toelichting;
    }

    public void setToelichting(final String toelichting) {
        this.toelichting = toelichting;
    }

    public String getResourceWeergave() {
        return resourceWeergave;
    }

    public void setResourceWeergave(final String resourceWeergave) {
        this.resourceWeergave = resourceWeergave;
    }

    public ZonedDateTime getAanmaakdatum() {
        return aanmaakdatum;
    }

    public void setAanmaakdatum(final ZonedDateTime aanmaakdatum) {
        this.aanmaakdatum = aanmaakdatum;
    }

    public AuditWijziging<?> getWijzigingen() {
        return wijzigingen;
    }

    public void setWijzigingen(final AuditWijziging<?> wijzigingen) {
        this.wijzigingen = wijzigingen;
    }
}
