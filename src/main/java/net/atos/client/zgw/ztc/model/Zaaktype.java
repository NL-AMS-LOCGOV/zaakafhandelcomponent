/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.ztc.model;

import java.net.URI;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;

import net.atos.client.zgw.shared.model.Vertrouwelijkheidaanduiding;
import net.atos.client.zgw.shared.util.URIUtil;

/**
 *
 */
public class Zaaktype {

    public static final int IDENTIFICATIE_MAX_LENGTH = 50;

    public static final int OMSCHRIJVING_MAX_LENGTH = 80;

    public static final int OMSCHRIJVING_GENERIEK_MAX_LENGTH = 80;

    public static final int HANDELING_INITIATOR_MAX_LENGTH = 20;

    public static final int ONDERWERP_MAX_LENGTH = 80;

    public static final int HANDELING_BEHANDELAAR_MAX_LENGTH = 20;

    public static final int TREFWOORD_MAX_LENGTH = 20;

    public static final int VERANTWOORDINGSRELATIE_MAX_LENGTH = 40;

    /**
     * URL-referentie naar dit object.
     * Dit is de unieke identificatie en locatie van dit object.
     */
    private URI url;

    /**
     * Unieke identificatie van het ZAAKTYPE binnen de CATALOGUS waarin het ZAAKTYPE voorkomt.
     * maxLength: {@link Zaaktype#IDENTIFICATIE_MAX_LENGTH}
     */
    private String identificatie;

    /**
     * Omschrijving van de aard van ZAAKen van het ZAAKTYPE.
     * maxLength: {@link Zaaktype#OMSCHRIJVING_MAX_LENGTH}
     * - required
     */
    private String omschrijving;

    /**
     * Algemeen gehanteerde omschrijving van de aard van ZAAKen van het ZAAKTYPE
     * maxLength: {@link Zaaktype#OMSCHRIJVING_GENERIEK_MAX_LENGTH}
     */
    private String omschrijvingGeneriek;

    /**
     * Aanduiding van de mate waarin zaakdossiers van ZAAKen van dit ZAAKTYPE voor de openbaarheid bestemd zijn.
     * Indien de zaak bij het aanmaken geen vertrouwelijkheidaanduiding krijgt, dan wordt deze waarde gezet.
     * - required
     */
    private Vertrouwelijkheidaanduiding vertrouwelijkheidaanduiding;

    /**
     * Een omschrijving van hetgeen beoogd is te bereiken met een zaak van dit zaaktype.
     * - required
     */
    private String doel;

    /**
     * Een omschrijving van de gebeurtenis die leidt tot het starten van een ZAAK van dit ZAAKTYPE
     * - required
     */
    private String aanleiding;

    /**
     * Een eventuele toelichting op dit zaaktype, zoals een beschrijving van het procesverloop op de hoofdlijnen.
     */
    private String toelichting;

    /**
     * Een aanduiding waarmee onderscheid wordt gemaakt tussen ZAAKTYPEn die Intern respectievelijk Extern ge\xEFnitieerd worden.
     * Indien van beide sprake kan zijn, dan prevaleert de externe initiatie
     * - required
     */
    private IndicatieInternExtern indicatieInternOfExtern;

    /**
     * Werkwoord dat hoort bij de handeling die de initiator verricht bij dit zaaktype.
     * Meestal 'aanvragen', 'indienen' of 'melden'.
     * Zie ook het IOB model op https://www.gemmaonline.nl/index.php/Imztc_2.1/doc/attribuutsoort/zaaktype.handeling_initiator
     * - required
     * - maxLength: {@link Zaaktype#HANDELING_INITIATOR_MAX_LENGTH}
     */
    private String handelingInitiator;

    /**
     * Het onderwerp van ZAAKen van dit ZAAKTYPE.
     * In veel gevallen nauw gerelateerd aan de product- of dienstnaam uit de Producten- en Dienstencatalogus (PDC).
     * Bijvoorbeeld: ''Evenementenvergunning'', ''Geboorte'', ''Klacht''.
     * Zie ook het IOB model op https://www.gemmaonline.nl/index.php/Imztc_2.1/doc/attribuutsoort/zaaktype.onderwer
     * - required
     * - maxLength: {@link Zaaktype#ONDERWERP_MAX_LENGTH}
     */
    private String onderwerp;

    /**
     * Werkwoord dat hoort bij de handeling die de behandelaar verricht bij het afdoen van ZAAKen van dit ZAAKTYPE.
     * Meestal 'behandelen', 'uitvoeren', 'vaststellen' of 'onderhouden'.
     * Zie ook het IOB model op https://www.gemmaonline.nl/index.php/Imztc_2.1/doc/attribuutsoort/zaaktype.handeling_behandelaar
     * - required
     * - maxLength: {@link Zaaktype#HANDELING_BEHANDELAAR_MAX_LENGTH}
     */
    private String handelingBehandelaar;

    /**
     * De periode waarbinnen volgens wet- en regelgeving een ZAAK van het ZAAKTYPE afgerond dient te zijn, in kalenderdagen.
     * - required
     */
    private Period doorlooptijd;

    /**
     * De periode waarbinnen verwacht wordt dat een ZAAK van het ZAAKTYPE afgerond wordt conform de geldende servicenormen van de zaakbehandelende organisatie(s).
     */
    private Period servicenorm;

    /**
     * Aanduiding die aangeeft of ZAAKen van dit mogelijk ZAAKTYPE kunnen worden opgeschort en/of aangehouden.
     * - required
     */
    private Boolean opschortingEnAanhoudingMogelijk;

    /**
     * Aanduiding die aangeeft of de Doorlooptijd behandeling van ZAAKen van dit ZAAKTYPE kan worden verlengd.
     * - required
     */
    private Boolean verlengingMogelijk;

    /**
     * De termijn in dagen waarmee de Doorlooptijd behandeling van ZAAKen van dit ZAAKTYPE kan worden verlengd.
     * Mag alleen een waarde bevatten als verlenging mogelijk is.
     */
    private Period verlengingstermijn;

    /**
     * Een trefwoord waarmee ZAAKen van het ZAAKTYPE kunnen worden gekarakteriseerd.
     * maxLength: {@link Zaaktype#TREFWOORD_MAX_LENGTH}
     */
    private List<String> trefwoorden;

    /**
     * Aanduiding of (het starten van) een ZAAK dit ZAAKTYPE gepubliceerd moet worden.
     * - required
     */
    private Boolean publicatieIndicatie;

    /**
     * De generieke tekst van de publicatie van ZAAKen van dit ZAAKTYPE.
     */
    private String publicatietekst;

    /**
     * De relatie tussen ZAAKen van dit ZAAKTYPE en de beleidsmatige en/of financiele verantwoording
     *
     * @maxLength: {@link Zaaktype#VERANTWOORDINGSRELATIE_MAX_LENGTH}
     */
    private List<String> verantwoordingsrelatie;

    /**
     * Het product of de dienst die door ZAAKen van dit ZAAKTYPE wordt voortgebracht.
     */
    private List<URI> productenOfDiensten;

    /**
     * URL-referentie naar een vanuit archiveringsoptiek onderkende groep processen met dezelfde kenmerken (PROCESTYPE in de Selectielijst API).
     */
    private URI selectielijstProcestype;

    /**
     * Het Referentieproces dat ten grondslag ligt aan dit ZAAKTYPE.
     * - required
     */
    private Referentieproces referentieproces;

    /**
     * URL-referentie naar de CATALOGUS waartoe dit ZAAKTYPE behoort.
     * - required
     */
    private URI catalogus;

    /**
     * URL-referenties naar de STATUSTYPEN die mogelijk zijn binnen
     */
    private Set<URI> statustypen;

    /**
     * URL-referenties naar de RESULTAATTYPEN die mogelijk zijn binnen dit ZAAKTYPE.
     */
    private Set<URI> resultaattypen;

    /**
     * URL-referenties naar de EIGENSCHAPPEN die aanwezig moeten zijn in ZAKEN van dit ZAAKTYPE.
     */
    private Set<URI> eigenschappen;

    /**
     * URL-referenties naar de INFORMATIEOBJECTTYPEN die mogelijk zijn binnen dit ZAAKTYPE.
     */
    private Set<URI> informatieobjecttypen;

    /**
     * URL-referenties naar de ROLTYPEN die mogelijk zijn binnen dit ZAAKTYPE.
     */
    private Set<URI> roltypen;

    /**
     * URL-referenties naar de BESLUITTYPEN die mogelijk zijn binnen dit ZAAKTYPE.
     * - required
     */
    private Set<URI> besluittypen;

    /**
     * De ZAAKTYPE(n) waaronder ZAAKen als deelzaak kunnen voorkomen bij ZAAKen van dit ZAAKTYPE.
     */
    private Set<URI> deelzaaktypen;

    /**
     * De ZAAKTYPEn van zaken die relevant zijn voor zaken van dit ZAAKTYPE.
     * - required
     */
    private List<ZaakTypenRelatie> gerelateerdeZaaktypen;

    /**
     * De datum waarop het is ontstaan.
     * - required
     */
    private LocalDate beginGeldigheid;

    /**
     * De datum waarop het is opgeheven.
     */
    private LocalDate eindeGeldigheid;

    /**
     * De datum waarop de (gewijzigde) kenmerken van het ZAAKTYPE geldig zijn geworden
     * - required
     */
    private LocalDate versiedatum;

    /**
     * Geeft aan of het object een concept betreft.
     * Concepten zijn niet-definitieve versies en zouden niet gebruikt moeten worden buiten deze API.
     */
    private Boolean concept;

    /**
     * Constructor for POST, PUT and PATCH requests
     */
    public Zaaktype() {
    }

    /**
     * Constructor with readOnly attributes for GET response
     */
    @JsonbCreator
    public Zaaktype(@JsonbProperty("url") final URI url,
            @JsonbProperty("statustypen") final Set<URI> statustypen,
            @JsonbProperty("resultaattypen") final Set<URI> resultaattypen,
            @JsonbProperty("eigenschappen") final Set<URI> eigenschappen,
            @JsonbProperty("informatieobjecttypen") final Set<URI> informatieobjecttypen,
            @JsonbProperty("roltypen") final Set<URI> roltypen,
            @JsonbProperty("concept") final Boolean concept) {
        this.url = url;
        this.statustypen = statustypen;
        this.resultaattypen = resultaattypen;
        this.eigenschappen = eigenschappen;
        this.informatieobjecttypen = informatieobjecttypen;
        this.roltypen = roltypen;
        this.concept = concept;
    }

    public URI getUrl() {
        return url;
    }

    public Set<URI> getStatustypen() {
        return statustypen;
    }

    public Set<URI> getResultaattypen() {
        return resultaattypen;
    }

    public Set<URI> getEigenschappen() {
        return eigenschappen;
    }

    public Set<URI> getInformatieobjecttypen() {
        return informatieobjecttypen;
    }

    public Set<URI> getRoltypen() {
        return roltypen;
    }

    public Boolean getConcept() {
        return concept;
    }

    public String getIdentificatie() {
        return identificatie;
    }

    public void setIdentificatie(final String identificatie) {
        this.identificatie = identificatie;
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

    public Vertrouwelijkheidaanduiding getVertrouwelijkheidaanduiding() {
        return vertrouwelijkheidaanduiding;
    }

    public void setVertrouwelijkheidaanduiding(final Vertrouwelijkheidaanduiding vertrouwelijkheidaanduiding) {
        this.vertrouwelijkheidaanduiding = vertrouwelijkheidaanduiding;
    }

    public String getDoel() {
        return doel;
    }

    public void setDoel(final String doel) {
        this.doel = doel;
    }

    public String getAanleiding() {
        return aanleiding;
    }

    public void setAanleiding(final String aanleiding) {
        this.aanleiding = aanleiding;
    }

    public String getToelichting() {
        return toelichting;
    }

    public void setToelichting(final String toelichting) {
        this.toelichting = toelichting;
    }

    public IndicatieInternExtern getIndicatieInternOfExtern() {
        return indicatieInternOfExtern;
    }

    public void setIndicatieInternOfExtern(final IndicatieInternExtern indicatieInternOfExtern) {
        this.indicatieInternOfExtern = indicatieInternOfExtern;
    }

    public String getHandelingInitiator() {
        return handelingInitiator;
    }

    public void setHandelingInitiator(final String handelingInitiator) {
        this.handelingInitiator = handelingInitiator;
    }

    public String getOnderwerp() {
        return onderwerp;
    }

    public void setOnderwerp(final String onderwerp) {
        this.onderwerp = onderwerp;
    }

    public String getHandelingBehandelaar() {
        return handelingBehandelaar;
    }

    public void setHandelingBehandelaar(final String handelingBehandelaar) {
        this.handelingBehandelaar = handelingBehandelaar;
    }

    public Period getDoorlooptijd() {
        // TODO Default doorlooptijd van 7 dagen weghalen
        // Er is op het moment een bug bij open zaak dat de verplichte waarde doorlooptijd niet opgeslagen
        // wordt bij het aanmaken van een nieuwe versie van het zaaktype. Vandaar even tijdelijk
        // een default doorlooptijd van 7 dagen als de doorlooptijd null is,
        // want deze kan op het moment als null binnenkomen.
        //
        // Bug open zaak: https://github.com/open-zaak/open-zaak/issues/1273
        return doorlooptijd != null ? doorlooptijd : Period.ofDays(7);
    }

    public void setDoorlooptijd(final Period doorlooptijd) {
        this.doorlooptijd = doorlooptijd;
    }

    public Period getServicenorm() {
        return servicenorm;
    }

    public void setServicenorm(final Period servicenorm) {
        this.servicenorm = servicenorm;
    }

    public boolean isServicenormBeschikbaar() {
        return this.servicenorm != null && !this.servicenorm.normalized().isZero();
    }

    public Boolean getOpschortingEnAanhoudingMogelijk() {
        return opschortingEnAanhoudingMogelijk;
    }

    public void setOpschortingEnAanhoudingMogelijk(final Boolean opschortingEnAanhoudingMogelijk) {
        this.opschortingEnAanhoudingMogelijk = opschortingEnAanhoudingMogelijk;
    }

    public Boolean getVerlengingMogelijk() {
        return verlengingMogelijk;
    }

    public void setVerlengingMogelijk(final Boolean verlengingMogelijk) {
        this.verlengingMogelijk = verlengingMogelijk;
    }

    public Period getVerlengingstermijn() {
        return verlengingstermijn;
    }

    public void setVerlengingstermijn(final Period verlengingstermijn) {
        this.verlengingstermijn = verlengingstermijn;
    }

    public List<String> getTrefwoorden() {
        return trefwoorden;
    }

    public void setTrefwoorden(final List<String> trefwoorden) {
        this.trefwoorden = trefwoorden;
    }

    public Boolean getPublicatieIndicatie() {
        return publicatieIndicatie;
    }

    public void setPublicatieIndicatie(final Boolean publicatieIndicatie) {
        this.publicatieIndicatie = publicatieIndicatie;
    }

    public String getPublicatietekst() {
        return publicatietekst;
    }

    public void setPublicatietekst(final String publicatietekst) {
        this.publicatietekst = publicatietekst;
    }

    public List<String> getVerantwoordingsrelatie() {
        return verantwoordingsrelatie;
    }

    public void setVerantwoordingsrelatie(final List<String> verantwoordingsrelatie) {
        this.verantwoordingsrelatie = verantwoordingsrelatie;
    }

    public List<URI> getProductenOfDiensten() {
        return productenOfDiensten;
    }

    public void setProductenOfDiensten(final List<URI> productenOfDiensten) {
        this.productenOfDiensten = productenOfDiensten;
    }

    public URI getSelectielijstProcestype() {
        return selectielijstProcestype;
    }

    public void setSelectielijstProcestype(final URI selectielijstProcestype) {
        this.selectielijstProcestype = selectielijstProcestype;
    }

    public Referentieproces getReferentieproces() {
        return referentieproces;
    }

    public void setReferentieproces(final Referentieproces referentieproces) {
        this.referentieproces = referentieproces;
    }

    public URI getCatalogus() {
        return catalogus;
    }

    public void setCatalogus(final URI catalogus) {
        this.catalogus = catalogus;
    }

    public Set<URI> getBesluittypen() {
        return besluittypen;
    }

    public void setBesluittypen(final Set<URI> besluittypen) {
        this.besluittypen = besluittypen;
    }

    public Set<URI> getDeelzaaktypen() {
        return deelzaaktypen;
    }

    public void setDeelzaaktypen(final Set<URI> deelzaaktypen) {
        this.deelzaaktypen = deelzaaktypen;
    }

    public List<ZaakTypenRelatie> getGerelateerdeZaaktypen() {
        return gerelateerdeZaaktypen;
    }

    public void setGerelateerdeZaaktypen(final List<ZaakTypenRelatie> gerelateerdeZaaktypen) {
        this.gerelateerdeZaaktypen = gerelateerdeZaaktypen;
    }

    public LocalDate getBeginGeldigheid() {
        return beginGeldigheid;
    }

    public void setBeginGeldigheid(final LocalDate beginGeldigheid) {
        this.beginGeldigheid = beginGeldigheid;
    }

    public LocalDate getEindeGeldigheid() {
        return eindeGeldigheid;
    }

    public void setEindeGeldigheid(final LocalDate eindeGeldigheid) {
        this.eindeGeldigheid = eindeGeldigheid;
    }

    public LocalDate getVersiedatum() {
        return versiedatum;
    }

    public void setVersiedatum(final LocalDate versiedatum) {
        this.versiedatum = versiedatum;
    }

    @JsonbTransient
    public boolean isNuGeldig() {
        return beginGeldigheid.isBefore(LocalDate.now().plusDays(1)) && (eindeGeldigheid == null || eindeGeldigheid.isAfter(LocalDate.now()));
    }

    @JsonbTransient
    public UUID getUUID() {
        return URIUtil.parseUUIDFromResourceURI(url);
    }
}
