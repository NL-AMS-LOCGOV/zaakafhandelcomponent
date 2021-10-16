/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.ztc.model;

import java.net.URI;
import java.util.Set;

public class Catalogus {

    /**
     * URL-referentie naar dit object. Dit is de unieke identificatie en locatie van dit object.
     * readOnly
     */
    private URI url;

    /**
     * Een afkorting waarmee wordt aangegeven voor welk domein in een CATALOGUS ZAAKTYPEn zijn uitgewerkt.
     * required, maxlength 5
     */
    private String domein;

    /**
     * Het door een kamer toegekend uniek nummer voor de INGESCHREVEN NIET-NATUURLIJK PERSOON die de eigenaar is van een CATALOGUS.
     * required, maxlength 9
     */
    private String rsin;

    /**
     * De naam van de contactpersoon die verantwoordelijk is voor het beheer van de CATALOGUS.
     * required, maxlength 40
     */
    private String contactpersoonBeheerNaam;

    /**
     * Het telefoonnummer van de contactpersoon die verantwoordelijk is voor het beheer van de CATALOGUS.
     * maxlength 20
     */
    private String contactpersoonBeheerTelefoonnummer;

    /**
     * Het emailadres van de contactpersoon die verantwoordelijk is voor het beheer van de CATALOGUS.
     * maxlength 254
     */
    private String contactpersoonBeheerEmailadres;

    /**
     * URL-referenties naar ZAAKTYPEn die in deze CATALOGUS worden ontsloten.
     * readOnly, uniqueItems
     */
    private Set<URI> zaaktypen;

    /**
     * URL-referenties naar BESLUITTYPEn die in deze CATALOGUS worden ontsloten.
     * readOnly, uniqueItems
     */
    private Set<URI> besluittypen;

    /**
     * URL-referenties naar INFORMATIEOBJECTTYPEn die in deze CATALOGUS worden ontsloten.
     * readOnly, uniqueItems
     */
    private Set<URI> informatieobjecttypen;

    public URI getUrl() {
        return url;
    }

    public void setUrl(final URI url) {
        this.url = url;
    }

    public String getDomein() {
        return domein;
    }

    public void setDomein(final String domein) {
        this.domein = domein;
    }

    public String getRsin() {
        return rsin;
    }

    public void setRsin(final String rsin) {
        this.rsin = rsin;
    }

    public String getContactpersoonBeheerNaam() {
        return contactpersoonBeheerNaam;
    }

    public void setContactpersoonBeheerNaam(final String contactpersoonBeheerNaam) {
        this.contactpersoonBeheerNaam = contactpersoonBeheerNaam;
    }

    public String getContactpersoonBeheerTelefoonnummer() {
        return contactpersoonBeheerTelefoonnummer;
    }

    public void setContactpersoonBeheerTelefoonnummer(final String contactpersoonBeheerTelefoonnummer) {
        this.contactpersoonBeheerTelefoonnummer = contactpersoonBeheerTelefoonnummer;
    }

    public String getContactpersoonBeheerEmailadres() {
        return contactpersoonBeheerEmailadres;
    }

    public void setContactpersoonBeheerEmailadres(final String contactpersoonBeheerEmailadres) {
        this.contactpersoonBeheerEmailadres = contactpersoonBeheerEmailadres;
    }

    public Set<URI> getZaaktypen() {
        return zaaktypen;
    }

    public void setZaaktypen(final Set<URI> zaaktypen) {
        this.zaaktypen = zaaktypen;
    }

    public Set<URI> getBesluittypen() {
        return besluittypen;
    }

    public void setBesluittypen(final Set<URI> besluittypen) {
        this.besluittypen = besluittypen;
    }

    public Set<URI> getInformatieobjecttypen() {
        return informatieobjecttypen;
    }

    public void setInformatieobjecttypen(final Set<URI> informatieobjecttypen) {
        this.informatieobjecttypen = informatieobjecttypen;
    }
}
