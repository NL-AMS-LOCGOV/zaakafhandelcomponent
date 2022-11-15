/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken.model.zoekobject;

import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

import net.atos.zac.zoeken.model.ZoekObject;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

public class DocumentZoekObject implements ZoekObject {

    @Field
    private String uuid;

    @Field("informatieobject_identificatie")
    private String identificatie;

    @Field("informatieobject_titel")
    private String titel;

    @Field("informatieobject_beschrijving")
    private String beschrijving;

    @Field("informatieobject_zaaktypeUuid")
    private String zaaktypeUuid;

    @Field("informatieobject_zaaktypeIdentificatie")
    private String zaaktypeIdentificatie;

    @Field("informatieobject_zaaktypeOmschrijving")
    private String zaaktypeOmschrijving;

    @Field("informatieobject_zaakId")
    private String zaakId;

    @Field("informatieobject_zaakUuid")
    private String zaakUuid;

    @Field("informatieobject_indicatieVergrendeld")
    private boolean zaakAfgehandeld;

    @Field("informatieobject_zaakRelatie")
    private String zaakRelatie;

    @Field("informatieobject_creatiedatum")
    private Date creatiedatum;

    @Field("informatieobject_registratiedatum")
    private Date registratiedatum;

    @Field("informatieobject_ontvangstdatum")
    private Date ontvangstdatum;

    @Field("informatieobject_verzenddatum")
    private Date verzenddatum;

    @Field("informatieobject_ondertekeningDatum")
    private Date ondertekeningDatum;

    @Field("informatieobject_vertrouwelijkheidaanduiding")
    private String vertrouwelijkheidaanduiding;

    @Field("informatieobject_auteur")
    private String auteur;

    @Field("informatieobject_status")
    private String status;

    @Field("informatieobject_formaat")
    private String formaat;

    @Field("informatieobject_versie")
    private long versie;

    @Field("informatieobject_bestandsnaam")
    private String bestandsnaam;

    @Field("informatieobject_bestandsomvang")
    private long bestandsomvang;

    @Field("informatieobject_documentType")
    private String documentType;

    @Field("informatieobject_ondertekeningSoort")
    private String ondertekeningSoort;

    @Field("informatieobject_indicatieOndertekend")
    private boolean indicatieOndertekend;

    @Field("informatieobject_inhoudUrl")
    private String inhoudUrl;

    @Field("informatieobject_indicatieVergrendeld")
    private boolean indicatieVergrendeld;

    @Field("informatieobject_vergrendeldDoorNaam")
    private String vergrendeldDoorNaam;

    @Field("informatieobject_vergrendeldDoorGebruikersnaam")
    private String vergrendeldDoorGebruikersnaam;

    public DocumentZoekObject() {
    }

    @Override
    public ZoekObjectType getType() {
        return ZoekObjectType.DOCUMENT;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getIdentificatie() {
        return identificatie;
    }

    public void setIdentificatie(final String identificatie) {
        this.identificatie = identificatie;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(final String titel) {
        this.titel = titel;
    }

    public String getBeschrijving() {
        return beschrijving;
    }

    public void setBeschrijving(final String beschrijving) {
        this.beschrijving = beschrijving;
    }

    public String getZaaktypeUuid() {
        return zaaktypeUuid;
    }

    public void setZaaktypeUuid(final String zaaktypeUuid) {
        this.zaaktypeUuid = zaaktypeUuid;
    }

    public String getZaaktypeIdentificatie() {
        return zaaktypeIdentificatie;
    }

    public void setZaaktypeIdentificatie(final String zaaktypeIdentificatie) {
        this.zaaktypeIdentificatie = zaaktypeIdentificatie;
    }

    public String getZaaktypeOmschrijving() {
        return zaaktypeOmschrijving;
    }

    public void setZaaktypeOmschrijving(final String zaaktypeOmschrijving) {
        this.zaaktypeOmschrijving = zaaktypeOmschrijving;
    }

    public String getZaakId() {
        return zaakId;
    }

    public void setZaakId(final String zaakId) {
        this.zaakId = zaakId;
    }

    public String getZaakUuid() {
        return zaakUuid;
    }

    public void setZaakUuid(final String zaakUuid) {
        this.zaakUuid = zaakUuid;
    }

    public String getZaakRelatie() {
        return zaakRelatie;
    }

    public void setZaakRelatie(final String zaakRelatie) {
        this.zaakRelatie = zaakRelatie;
    }

    public boolean isZaakAfgehandeld() {
        return zaakAfgehandeld;
    }

    public void setZaakAfgehandeld(final boolean zaakAfgehandeld) {
        this.zaakAfgehandeld = zaakAfgehandeld;
    }

    public Date getCreatiedatum() {
        return creatiedatum;
    }

    public void setCreatiedatum(final Date creatiedatum) {
        this.creatiedatum = creatiedatum;
    }

    public Date getRegistratiedatum() {
        return registratiedatum;
    }

    public void setRegistratiedatum(final Date registratiedatum) {
        this.registratiedatum = registratiedatum;
    }

    public Date getOntvangstdatum() {
        return ontvangstdatum;
    }

    public void setOntvangstdatum(final Date ontvangstdatum) {
        this.ontvangstdatum = ontvangstdatum;
    }

    public Date getVerzenddatum() {
        return verzenddatum;
    }

    public void setVerzenddatum(final Date verzenddatum) {
        this.verzenddatum = verzenddatum;
    }

    public Date getOndertekeningDatum() {
        return ondertekeningDatum;
    }

    public void setOndertekeningDatum(final Date ondertekeningDatum) {
        this.ondertekeningDatum = ondertekeningDatum;
    }

    public String getVertrouwelijkheidaanduiding() {
        return vertrouwelijkheidaanduiding;
    }

    public void setVertrouwelijkheidaanduiding(final String vertrouwelijkheidaanduiding) {
        this.vertrouwelijkheidaanduiding = vertrouwelijkheidaanduiding;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(final String auteur) {
        this.auteur = auteur;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getFormaat() {
        return formaat;
    }

    public void setFormaat(final String formaat) {
        this.formaat = formaat;
    }

    public long getVersie() {
        return versie;
    }

    public void setVersie(final long versie) {
        this.versie = versie;
    }

    public String getBestandsnaam() {
        return bestandsnaam;
    }

    public void setBestandsnaam(final String bestandsnaam) {
        this.bestandsnaam = bestandsnaam;
    }

    public long getBestandsomvang() {
        return bestandsomvang;
    }

    public void setBestandsomvang(final long bestandsomvang) {
        this.bestandsomvang = bestandsomvang;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(final String documentType) {
        this.documentType = documentType;
    }

    public String getOndertekeningSoort() {
        return ondertekeningSoort;
    }

    public void setOndertekeningSoort(final String ondertekeningSoort) {
        this.ondertekeningSoort = ondertekeningSoort;
    }

    public boolean isIndicatieOndertekend() {
        return indicatieOndertekend;
    }

    public void setIndicatieOndertekend(final boolean indicatieOndertekend) {
        this.indicatieOndertekend = indicatieOndertekend;
    }

    public String getInhoudUrl() {
        return inhoudUrl;
    }

    public void setInhoudUrl(final String inhoudUrl) {
        this.inhoudUrl = inhoudUrl;
    }

    public boolean isIndicatieVergrendeld() {
        return indicatieVergrendeld;
    }

    public void setIndicatieVergrendeld(final boolean indicatieVergrendeld) {
        this.indicatieVergrendeld = indicatieVergrendeld;
    }

    public String getVergrendeldDoorNaam() {
        return vergrendeldDoorNaam;
    }

    public void setVergrendeldDoorNaam(final String vergrendeldDoorNaam) {
        this.vergrendeldDoorNaam = vergrendeldDoorNaam;
    }

    public String getVergrendeldDoorGebruikersnaam() {
        return vergrendeldDoorGebruikersnaam;
    }

    public void setVergrendeldDoorGebruikersnaam(final String vergrendeldDoorGebruikersnaam) {
        this.vergrendeldDoorGebruikersnaam = vergrendeldDoorGebruikersnaam;
    }
}
