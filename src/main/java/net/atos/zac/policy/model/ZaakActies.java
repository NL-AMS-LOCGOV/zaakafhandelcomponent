/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.policy.model;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class ZaakActies {

    private final boolean opschorten;

    private final boolean verlengen;

    private final boolean hervatten;

    private final boolean afbreken;

    private final boolean afsluiten; // ToDo: Alle deelzaken moeten geslotenm zijn.

    private final boolean heropenen;

    private final boolean creeerenDocument;

    private final boolean toevoegenDocument;

    // Het kunnen ontkoppelen of verplaatsen van een document behorende bij de zaak
    private final boolean ontkoppelenDocument;

    // Het kunnen koppelen van de huidige zaak aan een andere zaak
    private final boolean koppelenZaak;

    // Het kunnen koppelen van een document of andere zaak aan de huidige zaak
    private final boolean koppelenAanZaak;

    private final boolean versturenEmail;

    private final boolean versturenOntvangstbevestiging;

    private final boolean toevoegenPersoon;

    private final boolean toevoegenBedrijf;

    private final boolean verwijderenInitiator;

    private final boolean wijzigenToekenning;

    private final boolean wijzigenData;

    private final boolean wijzigenOverig;

    private final boolean startenPlanItems;

    @JsonbCreator
    public ZaakActies(@JsonbProperty("opschorten") final boolean opschorten,
            @JsonbProperty("verlengen") final boolean verlengen,
            @JsonbProperty("hervatten") final boolean hervatten,
            @JsonbProperty("afbreken") final boolean afbreken,
            @JsonbProperty("afsluiten") final boolean afsluiten,
            @JsonbProperty("heropenen") final boolean heropenen,
            @JsonbProperty("creeerenDocument") final boolean creeerenDocument,
            @JsonbProperty("toevoegenDocument") final boolean toevoegenDocument,
            @JsonbProperty("ontkoppelenDocument") final boolean ontkoppelenDocument,
            @JsonbProperty("koppelenZaak") final boolean koppelenZaak,
            @JsonbProperty("koppelenAanZaak") final boolean koppelenAanZaak,
            @JsonbProperty("versturenEmail") final boolean versturenEmail,
            @JsonbProperty("versturenOntvangstbevestiging") final boolean versturenOntvangstbevestiging,
            @JsonbProperty("toevoegenPersoon") final boolean toevoegenPersoon,
            @JsonbProperty("toevoegenBedrijf") final boolean toevoegenBedrijf,
            @JsonbProperty("verwijderenInitiator") final boolean verwijderenInitiator,
            @JsonbProperty("wijzigenToekenning") final boolean wijzigenToekenning,
            @JsonbProperty("wijzigenData") final boolean wijzigenData,
            @JsonbProperty("wijzigenOverig") final boolean wijzigenOverig,
            @JsonbProperty("startenPlanItems") final boolean startenPlanItems) {
        this.opschorten = opschorten;
        this.verlengen = verlengen;
        this.hervatten = hervatten;
        this.afbreken = afbreken;
        this.afsluiten = afsluiten;
        this.heropenen = heropenen;
        this.creeerenDocument = creeerenDocument;
        this.toevoegenDocument = toevoegenDocument;
        this.ontkoppelenDocument = ontkoppelenDocument;
        this.koppelenZaak = koppelenZaak;
        this.koppelenAanZaak = koppelenAanZaak;
        this.versturenEmail = versturenEmail;
        this.versturenOntvangstbevestiging = versturenOntvangstbevestiging;
        this.toevoegenPersoon = toevoegenPersoon;
        this.toevoegenBedrijf = toevoegenBedrijf;
        this.verwijderenInitiator = verwijderenInitiator;
        this.wijzigenToekenning = wijzigenToekenning;
        this.wijzigenData = wijzigenData;
        this.wijzigenOverig = wijzigenOverig;
        this.startenPlanItems = startenPlanItems;
    }

    public boolean getOpschorten() {
        return opschorten;
    }

    public boolean getVerlengen() {
        return verlengen;
    }

    public boolean getHervatten() {
        return hervatten;
    }

    public boolean getAfbreken() {
        return afbreken;
    }

    public boolean getAfsluiten() {
        return afsluiten;
    }

    public boolean getHeropenen() {
        return heropenen;
    }

    public boolean getCreeerenDocument() {
        return creeerenDocument;
    }

    public boolean getToevoegenDocument() {
        return toevoegenDocument;
    }

    public boolean getOntkoppelenDocument() {
        return ontkoppelenDocument;
    }

    public boolean getVersturenEmail() {
        return versturenEmail;
    }

    public boolean getVersturenOntvangstbevestiging() {
        return versturenOntvangstbevestiging;
    }

    public boolean getKoppelenZaak() {
        return koppelenZaak;
    }

    public boolean getKoppelenAanZaak() {
        return koppelenAanZaak;
    }

    public boolean getToevoegenPersoon() {
        return toevoegenPersoon;
    }

    public boolean getToevoegenBedrijf() {
        return toevoegenBedrijf;
    }

    public boolean getVerwijderenInitiator() {
        return verwijderenInitiator;
    }

    public boolean getWijzigenToekenning() {
        return wijzigenToekenning;
    }

    public boolean getWijzigenData() {
        return wijzigenData;
    }

    public boolean getWijzigenOverig() {
        return wijzigenOverig;
    }

    public boolean getStartenPlanItems() {
        return startenPlanItems;
    }
}
