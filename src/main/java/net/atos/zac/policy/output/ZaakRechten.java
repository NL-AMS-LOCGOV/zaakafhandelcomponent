/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.policy.output;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class ZaakRechten {

    private final boolean lezen;

    private final boolean opschorten;

    private final boolean verlengen;

    private final boolean hervatten;

    private final boolean afbreken;

    // activeren van een UserEventListener in een CMMN model
    // handmatig afsluiten
    private final boolean voortzetten;

    private final boolean heropenen;

    private final boolean creeerenDocument;

    private final boolean toevoegenDocument;

    // koppelen van huidige zaak aan andere zaak
    // koppelen van andere zaak aan huidige zaak
    // koppelen van document aan huidige zaak
    private final boolean koppelen;

    private final boolean versturenEmail;

    private final boolean versturenOntvangstbevestiging;

    private final boolean toevoegenBAGObject;

    private final boolean toevoegenInitiatorPersoon;

    private final boolean toevoegenInitiatorBedrijf;

    private final boolean verwijderenInitiator;

    private final boolean toevoegenBetrokkenePersoon;

    private final boolean toevoegenBetrokkeneBedrijf;

    private final boolean verwijderenBetrokkene;

    private final boolean toekennen;

    private final boolean wijzigen;

    private final boolean aanmakenTaak;

    private final boolean vastleggenBesluit;

    private final boolean wijzigenBesluit;

    @JsonbCreator
    public ZaakRechten(
            @JsonbProperty("lezen") final boolean lezen,
            @JsonbProperty("opschorten") final boolean opschorten,
            @JsonbProperty("verlengen") final boolean verlengen,
            @JsonbProperty("hervatten") final boolean hervatten,
            @JsonbProperty("afbreken") final boolean afbreken,
            @JsonbProperty("voortzetten") final boolean voortzetten,
            @JsonbProperty("heropenen") final boolean heropenen,
            @JsonbProperty("creeeren_document") final boolean creeerenDocument,
            @JsonbProperty("toevoegen_document") final boolean toevoegenDocument,
            @JsonbProperty("koppelen") final boolean koppelen,
            @JsonbProperty("versturen_email") final boolean versturenEmail,
            @JsonbProperty("versturen_ontvangstbevestiging") final boolean versturenOntvangstbevestiging,
            @JsonbProperty("toevoegen_bag_object") final boolean toevoegenBAGObject,
            @JsonbProperty("toevoegen_initiator_persoon") final boolean toevoegenInitiatorPersoon,
            @JsonbProperty("toevoegen_initiator_bedrijf") final boolean toevoegenInitiatorBedrijf,
            @JsonbProperty("verwijderen_initiator") final boolean verwijderenInitiator,
            @JsonbProperty("toevoegen_betrokkene_persoon") final boolean toevoegenBetrokkenePersoon,
            @JsonbProperty("toevoegen_betrokkene_bedrijf") final boolean toevoegenBetrokkeneBedrijf,
            @JsonbProperty("verwijderen_betrokkene") final boolean verwijderenBetrokkene,
            @JsonbProperty("toekennen") final boolean toekennen,
            @JsonbProperty("wijzigen") final boolean wijzigen,
            @JsonbProperty("aanmaken_taak") final boolean aanmakenTaak,
            @JsonbProperty("vastleggen_besluit") final boolean vastleggenBesluit,
            @JsonbProperty("wijzigen_besluit") final boolean wijzigenBesluit) {
        this.lezen = lezen;
        this.opschorten = opschorten;
        this.verlengen = verlengen;
        this.hervatten = hervatten;
        this.afbreken = afbreken;
        this.voortzetten = voortzetten;
        this.heropenen = heropenen;
        this.creeerenDocument = creeerenDocument;
        this.toevoegenDocument = toevoegenDocument;
        this.koppelen = koppelen;
        this.versturenEmail = versturenEmail;
        this.versturenOntvangstbevestiging = versturenOntvangstbevestiging;
        this.toevoegenBAGObject = toevoegenBAGObject;
        this.toevoegenInitiatorPersoon = toevoegenInitiatorPersoon;
        this.toevoegenInitiatorBedrijf = toevoegenInitiatorBedrijf;
        this.verwijderenInitiator = verwijderenInitiator;
        this.toevoegenBetrokkenePersoon = toevoegenBetrokkenePersoon;
        this.toevoegenBetrokkeneBedrijf = toevoegenBetrokkeneBedrijf;
        this.verwijderenBetrokkene = verwijderenBetrokkene;
        this.toekennen = toekennen;
        this.wijzigen = wijzigen;
        this.aanmakenTaak = aanmakenTaak;
        this.vastleggenBesluit = vastleggenBesluit;
        this.wijzigenBesluit = wijzigenBesluit;
    }

    public boolean getLezen() {
        return lezen;
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

    public boolean getVoortzetten() {
        return voortzetten;
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

    public boolean getVersturenEmail() {
        return versturenEmail;
    }

    public boolean getVersturenOntvangstbevestiging() {
        return versturenOntvangstbevestiging;
    }

    public boolean getKoppelen() {
        return koppelen;
    }

    public boolean getToevoegenBAGObject() {
        return toevoegenBAGObject;
    }

    public boolean getToevoegenInitiatorPersoon() {
        return toevoegenInitiatorPersoon;
    }

    public boolean getToevoegenInitiatorBedrijf() {
        return toevoegenInitiatorBedrijf;
    }

    public boolean getVerwijderenInitiator() {
        return verwijderenInitiator;
    }

    public boolean getToevoegenBetrokkenePersoon() {
        return toevoegenBetrokkenePersoon;
    }

    public boolean getToevoegenBetrokkeneBedrijf() {
        return toevoegenBetrokkeneBedrijf;
    }

    public boolean getVerwijderenBetrokkene() {
        return verwijderenBetrokkene;
    }

    public boolean getToekennen() {
        return toekennen;
    }

    public boolean getWijzigen() {
        return wijzigen;
    }

    public boolean getAanmakenTaak() {
        return aanmakenTaak;
    }

    public boolean getVastleggenBesluit() {
        return vastleggenBesluit;
    }

    public boolean getWijzigenBesluit() {
        return wijzigenBesluit;
    }
}
