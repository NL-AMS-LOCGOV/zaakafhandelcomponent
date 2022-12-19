/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.klanten.model;

import java.net.URI;

import javax.ws.rs.QueryParam;

public class KlantListParameters {

    @QueryParam("bronorganisatie")
    private String bronorganisatie;

    @QueryParam("klantnummer")
    private String klantnummer;

    @QueryParam("bedrijfsnaam")
    private String bedrijfsnaam;

    @QueryParam("functie")
    private String functie;

    @QueryParam("achternaam")
    private String achternaam;

    @QueryParam("telefoonnummer")
    private String telefoonnummer;

    @QueryParam("emailadres")
    private String emailadres;

    @QueryParam("adres__straatnaam")
    private String adresStraatnaam;

    @QueryParam("adres__postcode")
    private String adresPostcode;

    @QueryParam("adres__woonplaatsNaam")
    private String adresWoonplaatsNaam;

    @QueryParam("adres__landcode")
    private String adresLandcode;

    @QueryParam("subject")
    private URI subject;

    @QueryParam("subjectType")
    private String subjectType;

    @QueryParam("subjectNatuurlijkPersoon__inpBsn")
    private String subjectNatuurlijkPersoonInpBsn;

    @QueryParam("subjectNatuurlijkPersoon__anpIdentificatie")
    private String subjectNatuurlijkPersoonAnpIdentificatie;

    @QueryParam("subjectNatuurlijkPersoon__inpA_nummer")
    private String subjectNatuurlijkPersoonInpANummer;

    @QueryParam("subjectNietNatuurlijkPersoon__innNnpId")
    private String subjectNietNatuurlijkPersoonInnNnpId;

    @QueryParam("subjectNietNatuurlijkPersoon__annIdentificatie")
    private String subjectNietNatuurlijkPersoonAnnIdentificatie;

    @QueryParam("subjectVestiging__vestigingsNummer")
    private String subjectVestigingVestigingsNummer;

    @QueryParam("page")
    private Integer page;

    public String getBronorganisatie() {
        return bronorganisatie;
    }

    public void setBronorganisatie(final String bronorganisatie) {
        this.bronorganisatie = bronorganisatie;
    }

    public String getKlantnummer() {
        return klantnummer;
    }

    public void setKlantnummer(final String klantnummer) {
        this.klantnummer = klantnummer;
    }

    public String getBedrijfsnaam() {
        return bedrijfsnaam;
    }

    public void setBedrijfsnaam(final String bedrijfsnaam) {
        this.bedrijfsnaam = bedrijfsnaam;
    }

    public String getFunctie() {
        return functie;
    }

    public void setFunctie(final String functie) {
        this.functie = functie;
    }

    public String getAchternaam() {
        return achternaam;
    }

    public void setAchternaam(final String achternaam) {
        this.achternaam = achternaam;
    }

    public String getTelefoonnummer() {
        return telefoonnummer;
    }

    public void setTelefoonnummer(final String telefoonnummer) {
        this.telefoonnummer = telefoonnummer;
    }

    public String getEmailadres() {
        return emailadres;
    }

    public void setEmailadres(final String emailadres) {
        this.emailadres = emailadres;
    }

    public String getAdresStraatnaam() {
        return adresStraatnaam;
    }

    public void setAdresStraatnaam(final String adresStraatnaam) {
        this.adresStraatnaam = adresStraatnaam;
    }

    public String getAdresPostcode() {
        return adresPostcode;
    }

    public void setAdresPostcode(final String adresPostcode) {
        this.adresPostcode = adresPostcode;
    }

    public String getAdresWoonplaatsNaam() {
        return adresWoonplaatsNaam;
    }

    public void setAdresWoonplaatsNaam(final String adresWoonplaatsNaam) {
        this.adresWoonplaatsNaam = adresWoonplaatsNaam;
    }

    public String getAdresLandcode() {
        return adresLandcode;
    }

    public void setAdresLandcode(final String adresLandcode) {
        this.adresLandcode = adresLandcode;
    }

    public URI getSubject() {
        return subject;
    }

    public void setSubject(final URI subject) {
        this.subject = subject;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(final String subjectType) {
        this.subjectType = subjectType;
    }

    public String getSubjectNatuurlijkPersoonInpBsn() {
        return subjectNatuurlijkPersoonInpBsn;
    }

    public void setSubjectNatuurlijkPersoonInpBsn(final String subjectNatuurlijkPersoonInpBsn) {
        this.subjectNatuurlijkPersoonInpBsn = subjectNatuurlijkPersoonInpBsn;
    }

    public String getSubjectNatuurlijkPersoonAnpIdentificatie() {
        return subjectNatuurlijkPersoonAnpIdentificatie;
    }

    public void setSubjectNatuurlijkPersoonAnpIdentificatie(final String subjectNatuurlijkPersoonAnpIdentificatie) {
        this.subjectNatuurlijkPersoonAnpIdentificatie = subjectNatuurlijkPersoonAnpIdentificatie;
    }

    public String getSubjectNatuurlijkPersoonInpANummer() {
        return subjectNatuurlijkPersoonInpANummer;
    }

    public void setSubjectNatuurlijkPersoonInpANummer(final String subjectNatuurlijkPersoonInpANummer) {
        this.subjectNatuurlijkPersoonInpANummer = subjectNatuurlijkPersoonInpANummer;
    }

    public String getSubjectNietNatuurlijkPersoonInnNnpId() {
        return subjectNietNatuurlijkPersoonInnNnpId;
    }

    public void setSubjectNietNatuurlijkPersoonInnNnpId(final String subjectNietNatuurlijkPersoonInnNnpId) {
        this.subjectNietNatuurlijkPersoonInnNnpId = subjectNietNatuurlijkPersoonInnNnpId;
    }

    public String getSubjectNietNatuurlijkPersoonAnnIdentificatie() {
        return subjectNietNatuurlijkPersoonAnnIdentificatie;
    }

    public void setSubjectNietNatuurlijkPersoonAnnIdentificatie(
            final String subjectNietNatuurlijkPersoonAnnIdentificatie) {
        this.subjectNietNatuurlijkPersoonAnnIdentificatie = subjectNietNatuurlijkPersoonAnnIdentificatie;
    }

    public String getSubjectVestigingVestigingsNummer() {
        return subjectVestigingVestigingsNummer;
    }

    public void setSubjectVestigingVestigingsNummer(final String subjectVestigingVestigingsNummer) {
        this.subjectVestigingVestigingsNummer = subjectVestigingVestigingsNummer;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(final Integer page) {
        this.page = page;
    }
}
