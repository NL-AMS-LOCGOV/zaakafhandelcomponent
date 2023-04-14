/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;

import static java.util.stream.Collectors.joining;

import java.net.URI;
import java.time.LocalDate;
import java.util.Set;

import javax.ws.rs.QueryParam;

import org.apache.commons.collections4.CollectionUtils;

import net.atos.client.zgw.shared.model.AbstractListParameters;
import net.atos.client.zgw.shared.model.Archiefnominatie;
import net.atos.client.zgw.shared.model.Vertrouwelijkheidaanduiding;
import net.atos.client.zgw.ztc.model.AardVanRol;

/**
 *
 */
public class ZaakListParameters extends AbstractListParameters {

    /**
     * De unieke identificatie van de ZAAK binnen de organisatie die verantwoordelijk is voor de behandeling van de ZAAK.
     */
    @QueryParam("identificatie")
    private String identificatie;

    /**
     * Het RSIN van de Niet-natuurlijk persoon zijnde de organisatie die de zaak heeft gecreeerd.
     * Dit moet een geldig RSIN zijn van 9 nummers en voldoen aan https://nl.wikipedia.org/wiki/Burgerservicenummer#11-proef
     */
    @QueryParam("bronorganisatie")
    private String bronorganisatie;

    /**
     * URL-referentie naar het ZAAKTYPE (in de Catalogi API) in de CATALOGUS waar deze voorkomt
     */
    @QueryParam("zaaktype")
    private URI zaaktype;

    /**
     * Aanduiding of het zaakdossier blijvend bewaard of na een bepaalde termijn vernietigd moet worden.
     */
    private Archiefnominatie archiefnominatie;

    private Set<Archiefnominatie> archiefnominatieIn;

    /**
     * De datum waarop het gearchiveerde zaakdossier vernietigd moet worden dan wel overgebracht moet worden naar een archiefbewaarplaats.
     * Wordt automatisch berekend bij het aanmaken of wijzigen van een RESULTAAT aan deze ZAAK indien nog leeg.
     */
    @QueryParam("archiefactiedatum")
    private LocalDate archiefactiedatum;

    @QueryParam("archiefactiedatum__lt")
    private LocalDate archiefactiedatumLessThan;

    @QueryParam("archiefactiedatum__gt")
    private LocalDate archiefactiedatumGreaterThan;

    /**
     * Aanduiding of het zaakdossier blijvend bewaard of na een bepaalde termijn vernietigd moet worden.
     */
    private Archiefstatus archiefstatus;

    private Set<Archiefstatus> archiefstatusIn;

    /**
     * De datum waarop met de uitvoering van de zaak is gestart
     */
    @QueryParam("startdatum")
    private LocalDate startdatum;

    @QueryParam("startdatum__gt")
    private LocalDate startdatumGreaterThan;

    @QueryParam("startdatum__gte")
    private LocalDate startdatumGreaterThanOrEqual;

    @QueryParam("startdatum__lt")
    private LocalDate startdatumLessThan;

    @QueryParam("startdatum__lte")
    private LocalDate startdatumLessThanOrEqual;

    /**
     * Type van de `betrokkene`
     */
    private BetrokkeneType rolBetrokkeneType;

    /**
     * URL-referentie naar een betrokkene gerelateerd aan de ZAAK.
     */
    @QueryParam("rol__betrokkene")
    private URI rolBetrokkene;

    /**
     * Algemeen gehanteerde benaming van de aard van de ROL, afgeleid uit het ROLTYPE.
     */
    private AardVanRol rolOmschrijvingGeneriek;

    /**
     * Zaken met een vertrouwelijkheidaanduiding die beperkter is dan de aangegeven aanduiding worden uit de resultaten gefiltered.
     */
    private Vertrouwelijkheidaanduiding maximaleVertrouwelijkheidaanduiding;

    /**
     * Het burgerservicenummer, bedoeld in artikel 1.1 van de Wet algemene bepalingen burgerservicenummer.
     */
    @QueryParam("rol__betrokkeneIdentificatie__natuurlijkPersoon__inpBsn")
    private String rolBetrokkeneIdentificatieNatuurlijkPersoonInpBsn;

    /**
     * Een korte unieke aanduiding van de MEDEWERKER.
     */
    @QueryParam("rol__betrokkeneIdentificatie__medewerker__identificatie")
    private String rolBetrokkeneIdentificatieMedewerkerIdentificatie;

    /**
     * Een korte identificatie van de organisatorische eenheid.
     */
    @QueryParam("rol__betrokkeneIdentificatie__organisatorischeEenheid__identificatie")
    private String rolBetrokkeneIdentificatieOrganisatorischeEenheidIdentificatie;

    /**
     * Which field to use when ordering the results,
     * [field] voor asc en -[field] voor desc
     */
    @QueryParam("ordering")
    private String ordering;

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

    public URI getZaaktype() {
        return zaaktype;
    }

    public void setZaaktype(final URI zaaktype) {
        this.zaaktype = zaaktype;
    }

    @QueryParam("archiefnominatie")
    public String getArchiefnominatie() {
        return archiefnominatie != null ? archiefnominatie.getValue() : null;
    }

    public void setArchiefnominatie(final Archiefnominatie archiefnominatie) {
        this.archiefnominatie = archiefnominatie;
    }

    @QueryParam("archiefnominatie__in")
    public String getArchiefnominatieIn() {
        if (CollectionUtils.isNotEmpty(archiefnominatieIn)) {
            return archiefnominatieIn.stream()
                    .map(Archiefnominatie::getValue)
                    .collect(joining(","));
        } else {
            return null;
        }
    }

    public void setArchiefnominatieIn(final Set<Archiefnominatie> archiefnominatieIn) {
        this.archiefnominatieIn = archiefnominatieIn;
    }

    public LocalDate getArchiefactiedatum() {
        return archiefactiedatum;
    }

    public void setArchiefactiedatum(final LocalDate archiefactiedatum) {
        this.archiefactiedatum = archiefactiedatum;
    }

    public LocalDate getArchiefactiedatumLessThan() {
        return archiefactiedatumLessThan;
    }

    public void setArchiefactiedatumLessThan(final LocalDate archiefactiedatumLessThan) {
        this.archiefactiedatumLessThan = archiefactiedatumLessThan;
    }

    public LocalDate getArchiefactiedatumGreaterThan() {
        return archiefactiedatumGreaterThan;
    }

    public void setArchiefactiedatumGreaterThan(final LocalDate archiefactiedatumGreaterThan) {
        this.archiefactiedatumGreaterThan = archiefactiedatumGreaterThan;
    }

    @QueryParam("archiefstatus")
    public String getArchiefstatus() {
        return archiefstatus != null ? archiefstatus.toValue() : null;
    }

    public void setArchiefstatus(final Archiefstatus archiefstatus) {
        this.archiefstatus = archiefstatus;
    }

    @QueryParam("archiefstatus__in")
    public String getArchiefstatusIn() {
        if (CollectionUtils.isNotEmpty(archiefstatusIn)) {
            return archiefstatusIn.stream()
                    .map(Archiefstatus::toValue)
                    .collect(joining(","));
        } else {
            return null;
        }
    }

    public void setArchiefstatusIn(final Set<Archiefstatus> archiefstatusIn) {
        this.archiefstatusIn = archiefstatusIn;
    }

    public LocalDate getStartdatum() {
        return startdatum;
    }

    public void setStartdatum(final LocalDate startdatum) {
        this.startdatum = startdatum;
    }

    public LocalDate getStartdatumGreaterThan() {
        return startdatumGreaterThan;
    }

    public void setStartdatumGreaterThan(final LocalDate startdatumGreaterThan) {
        this.startdatumGreaterThan = startdatumGreaterThan;
    }

    public LocalDate getStartdatumGreaterThanOrEqual() {
        return startdatumGreaterThanOrEqual;
    }

    public void setStartdatumGreaterThanOrEqual(final LocalDate startdatumGreaterThanOrEqual) {
        this.startdatumGreaterThanOrEqual = startdatumGreaterThanOrEqual;
    }

    public LocalDate getStartdatumLessThan() {
        return startdatumLessThan;
    }

    public void setStartdatumLessThan(final LocalDate startdatumLessThan) {
        this.startdatumLessThan = startdatumLessThan;
    }

    public LocalDate getStartdatumLessThanOrEqual() {
        return startdatumLessThanOrEqual;
    }

    public void setStartdatumLessThanOrEqual(final LocalDate startdatumLessThanOrEqual) {
        this.startdatumLessThanOrEqual = startdatumLessThanOrEqual;
    }

    @QueryParam("rol__betrokkeneType")
    public String getRolBetrokkeneType() {
        return rolBetrokkeneType != null ? rolBetrokkeneType.toValue() : null;
    }

    public void setRolBetrokkeneType(final BetrokkeneType rolBetrokkeneType) {
        this.rolBetrokkeneType = rolBetrokkeneType;
    }

    public URI getRolBetrokkene() {
        return rolBetrokkene;
    }

    public void setRolBetrokkene(final URI rolBetrokkene) {
        this.rolBetrokkene = rolBetrokkene;
    }

    @QueryParam("rol__omschrijvingGeneriek")
    public String getRolOmschrijvingGeneriek() {
        return rolOmschrijvingGeneriek != null ? rolOmschrijvingGeneriek.toValue() : null;
    }

    public void setRolOmschrijvingGeneriek(final AardVanRol rolOmschrijvingGeneriek) {
        this.rolOmschrijvingGeneriek = rolOmschrijvingGeneriek;
    }

    @QueryParam("maximaleVertrouwelijkheidaanduiding")
    public String getMaximaleVertrouwelijkheidaanduiding() {
        return maximaleVertrouwelijkheidaanduiding != null ? maximaleVertrouwelijkheidaanduiding.toValue() : null;
    }

    public void setMaximaleVertrouwelijkheidaanduiding(final Vertrouwelijkheidaanduiding maximaleVertrouwelijkheidaanduiding) {
        this.maximaleVertrouwelijkheidaanduiding = maximaleVertrouwelijkheidaanduiding;
    }

    public String getRolBetrokkeneIdentificatieNatuurlijkPersoonInpBsn() {
        return rolBetrokkeneIdentificatieNatuurlijkPersoonInpBsn;
    }

    public void setRolBetrokkeneIdentificatieNatuurlijkPersoonInpBsn(final String rolBetrokkeneIdentificatieNatuurlijkPersoonInpBsn) {
        this.rolBetrokkeneIdentificatieNatuurlijkPersoonInpBsn = rolBetrokkeneIdentificatieNatuurlijkPersoonInpBsn;
    }

    public String getRolBetrokkeneIdentificatieMedewerkerIdentificatie() {
        return rolBetrokkeneIdentificatieMedewerkerIdentificatie;
    }

    public void setRolBetrokkeneIdentificatieMedewerkerIdentificatie(final String rolBetrokkeneIdentificatieMedewerkerIdentificatie) {
        this.rolBetrokkeneIdentificatieMedewerkerIdentificatie = rolBetrokkeneIdentificatieMedewerkerIdentificatie;
    }

    public String getRolBetrokkeneIdentificatieOrganisatorischeEenheidIdentificatie() {
        return rolBetrokkeneIdentificatieOrganisatorischeEenheidIdentificatie;
    }

    public void setRolBetrokkeneIdentificatieOrganisatorischeEenheidIdentificatie(final String rolBetrokkeneIdentificatieOrganisatorischeEenheidIdentificatie) {
        this.rolBetrokkeneIdentificatieOrganisatorischeEenheidIdentificatie = rolBetrokkeneIdentificatieOrganisatorischeEenheidIdentificatie;
    }

    public String getOrdering() {
        return ordering;
    }

    public void setOrdering(final String ordering) {
        this.ordering = ordering;
    }
}
