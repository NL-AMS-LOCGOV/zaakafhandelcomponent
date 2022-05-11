/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.drc.model;

import static net.atos.client.zgw.shared.util.DateTimeUtil.DATE_TIME_FORMAT_WITH_MILLISECONDS;

import java.net.URI;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbTransient;

import net.atos.client.zgw.shared.model.Vertrouwelijkheidaanduiding;
import net.atos.zac.util.UriUtil;

/**
 *
 */
public abstract class AbstractEnkelvoudigInformatieobject {

    public static final int IDENTIFICATIE_MAX_LENGTH = 40;

    public static final int TITEL_MAX_LENGTH = 200;

    public static final int BESTANDSNAAM_MAX_LENGTH = 255;

    public static final int FORMAAT_MAX_LENGTH = 255;

    public static final int AUTEUR_MAX_LENGTH = 200;

    public static final int BESCHRIJVING_MAX_LENGTH = 1000;

    /**
     * URL-referentie naar dit object. Dit is de unieke identificatie en locatie van dit object.
     */
    private URI url;

    /**
     * Een binnen een gegeven context ondubbelzinnige referentie naar het INFORMATIEOBJECT.
     * maxLength: {@link AbstractEnkelvoudigInformatieobject#IDENTIFICATIE_MAX_LENGTH}
     */
    private String identificatie;

    /**
     * Het RSIN van de Niet-natuurlijk persoon zijnde de organisatie die het informatieobject heeft gecreeerd of heeft ontvangen
     * en als eerste in een samenwerkingsketen heeft vastgelegd.
     */
    private String bronorganisatie;

    /**
     * Een datum of een gebeurtenis in de levenscyclus van het INFORMATIEOBJECT
     */
    private LocalDate creatiedatum;

    /**
     * De naam waaronder het INFORMATIEOBJECT formeel bekend is.
     * maxLength: {@link AbstractEnkelvoudigInformatieobject#TITEL_MAX_LENGTH}
     */
    private String titel;

    /**
     * Aanduiding van de mate waarin het INFORMATIEOBJECT voor de openbaarheid bestemd is
     */
    private Vertrouwelijkheidaanduiding vertrouwelijkheidaanduiding;

    /**
     * De persoon of organisatie die in de eerste plaats verantwoordelijk is voor het creeren van de inhoud van het INFORMATIEOBJECT
     * maxLength: {@link AbstractEnkelvoudigInformatieobject#AUTEUR_MAX_LENGTH}
     */
    private String auteur;

    /**
     * Aanduiding van de stand van zaken van een INFORMATIEOBJECT.
     * De waarden ''in bewerking'' en ''ter vaststelling'' komen niet voor als het attribuut `ontvangstdatum` van een waarde is voorzien.
     * Wijziging van de Status in ''gearchiveerd'' impliceert dat het informatieobject een duurzaam, niet-wijzigbaar Formaat dient te hebben
     */
    private InformatieobjectStatus status;

    /**
     * Het "Media Type" (voorheen "MIME type") voor de wijze waaropde inhoud van het INFORMATIEOBJECT is vastgelegd in een computerbestand.
     * Voorbeeld: `application/msword`. Zie: https://www.iana.org/assignments/media-types/media-types.xhtml
     * maxLength: {@link AbstractEnkelvoudigInformatieobject#FORMAAT_MAX_LENGTH}
     */
    private String formaat;

    /**
     * Een ISO 639-2/B taalcode waarin de inhoud van het INFORMATIEOBJECT is vastgelegd.
     * Voorbeeld: `nld`. Zie: https://www.iso.org/standard/4767.html
     * maxLength: 3
     * minLength: 3
     */
    private String taal;

    /**
     * Het (automatische) versienummer van het INFORMATIEOBJECT.
     * Deze begint bij 1 als het INFORMATIEOBJECT aangemaakt wordt.
     */
    private Integer versie;

    /**
     * Een datumtijd in ISO8601 formaat waarop deze versie van het INFORMATIEOBJECT is aangemaakt of gewijzigd.
     */
    @JsonbDateFormat(DATE_TIME_FORMAT_WITH_MILLISECONDS)
    private ZonedDateTime beginRegistratie;

    /**
     * De naam van het fysieke bestand waarin de inhoud van het informatieobject is vastgelegd, inclusief extensie.
     * maxLength: {@link AbstractEnkelvoudigInformatieobject#BESTANDSNAAM_MAX_LENGTH}
     */
    private String bestandsnaam;

    /**
     * Aantal bytes dat de inhoud van INFORMATIEOBJECT in beslag neemt.
     */
    private Long Bestandsomvang;

    /**
     * De URL waarmee de inhoud van het INFORMATIEOBJECT op te vragen
     */
    private URI link;

    /**
     * Een generieke beschrijving van de inhoud van het INFORMATIEOBJECT.
     * maxLength: {@link AbstractEnkelvoudigInformatieobject#BESCHRIJVING_MAX_LENGTH}
     */
    private String beschrijving;

    /**
     * De datum waarop het INFORMATIEOBJECT ontvangen is.
     * Verplicht te registreren voor INFORMATIEOBJECTen die van buiten de zaakbehandelende organisatie(s) ontvangen zijn.
     * Ontvangst en verzending is voorbehouden aan documenten die van of naar andere personen ontvangen of verzonden zijn
     * waarbij die personen niet deel uit maken van de behandeling van de zaak waarin het document een rol speelt.
     */
    private LocalDate ontvangstdatum;

    /**
     * De datum waarop het INFORMATIEOBJECT verzonden is, zoals deze op het INFORMATIEOBJECT vermeld is.
     * Dit geldt voor zowel inkomende als uitgaande INFORMATIEOBJECTen.
     * Eenzelfde informatieobject kan niet tegelijk inkomend en uitgaand zijn.
     * Ontvangst en verzending is voorbehouden aan documenten die van of naar andere personen ontvangen of verzonden zijn
     * waarbij die personen niet deel uit maken van de behandeling van de zaak waarin het document een rol speelt.
     */
    private LocalDate verzenddatum;

    /**
     * Indicatie of er beperkingen gelden aangaande het gebruik van het informatieobject anders dan raadpleging.
     * Dit veld mag `null` zijn om aan te geven dat de indicatie nog niet bekend is.
     * Als de indicatie gezet is, dan kan je de gebruiksrechten die van toepassing zijn raadplegen via de GEBRUIKSRECHTen resource.
     */
    private Boolean indicatieGebruiksrecht;


    /**
     * Aanduiding van de rechtskracht van een informatieobject.
     * Mag niet van een waarde zijn voorzien als de `status` de waarde 'in bewerking' of 'ter vaststelling' heeft.
     */
    private Ondertekening ondertekening;

    /**
     * Uitdrukking van mate van volledigheid en onbeschadigd zijn van digitaal bestand.
     */
    private Integriteit integriteit;

    /**
     * URL-referentie naar het INFORMATIEOBJECTTYPE (in de Catalogi API).
     */
    private URI informatieobjecttype;

    /**
     * Geeft aan of het document gelocked is. Alleen als een document gelocked is, mogen er aanpassingen gemaakt worden.
     */
    private Boolean locked;

    /**
     * Constructor for PATCH request
     */
    public AbstractEnkelvoudigInformatieobject() {
    }

    /**
     * Constructor with required attributes for POST request
     */
    public AbstractEnkelvoudigInformatieobject(final String bronorganisatie, final LocalDate creatiedatum, final String titel, final String auteur,
            final String taal,
            final URI informatieobjecttype) {
        this.bronorganisatie = bronorganisatie;
        this.creatiedatum = creatiedatum;
        this.titel = titel;
        this.auteur = auteur;
        this.taal = taal;
        this.informatieobjecttype = informatieobjecttype;
    }

    /**
     * Constructor with readOnly attributes for GET response
     */
    public AbstractEnkelvoudigInformatieobject(final URI url, final Integer versie, final ZonedDateTime beginRegistratie, final Long bestandsomvang,
            final Boolean locked) {
        this.url = url;
        this.versie = versie;
        this.beginRegistratie = beginRegistratie;
        Bestandsomvang = bestandsomvang;
        this.locked = locked;
    }

    public URI getUrl() {
        return url;
    }

    public String getIdentificatie() {
        return identificatie;
    }

    public void setIdentificatie(final String identificatie) {
        this.identificatie = identificatie;
    }

    public String getBronorganisatie() {
        return bronorganisatie;
    }

    public void setBronorganisatie(final String bronorganisatie) {
        this.bronorganisatie = bronorganisatie;
    }

    public LocalDate getCreatiedatum() {
        return creatiedatum;
    }

    public void setCreatiedatum(final LocalDate creatiedatum) {
        this.creatiedatum = creatiedatum;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(final String titel) {
        this.titel = titel;
    }

    public Vertrouwelijkheidaanduiding getVertrouwelijkheidaanduiding() {
        return vertrouwelijkheidaanduiding;
    }

    public void setVertrouwelijkheidaanduiding(final Vertrouwelijkheidaanduiding vertrouwelijkheidaanduiding) {
        this.vertrouwelijkheidaanduiding = vertrouwelijkheidaanduiding;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(final String auteur) {
        this.auteur = auteur;
    }

    public InformatieobjectStatus getStatus() {
        return status;
    }

    public void setStatus(final InformatieobjectStatus status) {
        this.status = status;
    }

    public String getFormaat() {
        return formaat;
    }

    public void setFormaat(final String formaat) {
        this.formaat = formaat;
    }

    public String getTaal() {
        return taal;
    }

    public void setTaal(final String taal) {
        this.taal = taal;
    }

    public Integer getVersie() {
        return versie;
    }

    public ZonedDateTime getBeginRegistratie() {
        return beginRegistratie;
    }

    public String getBestandsnaam() {
        return bestandsnaam;
    }

    public void setBestandsnaam(final String bestandsnaam) {
        this.bestandsnaam = bestandsnaam;
    }

    public Long getBestandsomvang() {
        return Bestandsomvang;
    }

    public URI getLink() {
        return link;
    }

    public void setLink(final URI link) {
        this.link = link;
    }

    public String getBeschrijving() {
        return beschrijving;
    }

    public void setBeschrijving(final String beschrijving) {
        this.beschrijving = beschrijving;
    }

    public LocalDate getOntvangstdatum() {
        return ontvangstdatum;
    }

    public void setOntvangstdatum(final LocalDate ontvangstdatum) {
        this.ontvangstdatum = ontvangstdatum;
    }

    public LocalDate getVerzenddatum() {
        return verzenddatum;
    }

    public void setVerzenddatum(final LocalDate verzenddatum) {
        this.verzenddatum = verzenddatum;
    }

    public Boolean getIndicatieGebruiksrecht() {
        return indicatieGebruiksrecht;
    }

    public void setIndicatieGebruiksrecht(final Boolean indicatieGebruiksrecht) {
        this.indicatieGebruiksrecht = indicatieGebruiksrecht;
    }

    public Ondertekening getOndertekening() {
        return ondertekening;
    }

    public void setOndertekening(final Ondertekening ondertekening) {
        this.ondertekening = ondertekening;
    }

    public Integriteit getIntegriteit() {
        return integriteit;
    }

    public void setIntegriteit(final Integriteit integriteit) {
        this.integriteit = integriteit;
    }

    public URI getInformatieobjecttype() {
        return informatieobjecttype;
    }

    public void setInformatieobjecttype(final URI informatieobjecttype) {
        this.informatieobjecttype = informatieobjecttype;
    }

    public Boolean getLocked() {
        return locked;
    }

    @JsonbTransient
    public UUID getUUID() {
        return UriUtil.uuidFromURI(getInformatieobjecttype());
    }
}
