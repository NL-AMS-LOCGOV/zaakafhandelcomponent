/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken.model.zoekobject;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.solr.client.solrj.beans.Field;

import net.atos.client.zgw.drc.model.InformatieobjectStatus;
import net.atos.zac.zoeken.model.DocumentIndicatie;
import net.atos.zac.zoeken.model.ZoekObject;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

public class DocumentZoekObject implements ZoekObject {

    @Field("id")
    private String uuid;

    @Field
    private String type;

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
    private String zaakIdentificatie;

    @Field("informatieobject_zaakUuid")
    private String zaakUuid;

    @Field("informatieobject_zaakAfgehandeld")
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

    @Field("informatieobject_ondertekeningSoort")
    private String ondertekeningSoort;

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

    @Field("informatieobject_inhoudUrl")
    private String inhoudUrl;

    @Field("informatieobject_vergrendeldDoorNaam")
    private String vergrendeldDoorNaam;

    @Field("informatieobject_vergrendeldDoorGebruikersnaam")
    private String vergrendeldDoorGebruikersnaam;

    @Field("informatieobject_indicaties")
    private List<String> indicaties;

    @Field("informatieobject_indicaties_sort")
    private long indicatiesVolgorde;

    public DocumentZoekObject() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getObjectId() {
        return getUuid();
    }

    @Override
    public ZoekObjectType getType() {
        return ZoekObjectType.valueOf(type);
    }

    public void setType(final ZoekObjectType type) {
        this.type = type.toString();
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

    public String getZaakIdentificatie() {
        return zaakIdentificatie;
    }

    public void setZaakIdentificatie(final String zaakIdentificatie) {
        this.zaakIdentificatie = zaakIdentificatie;
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

    public InformatieobjectStatus getStatus() {
        return status != null ? InformatieobjectStatus.valueOf(status) : null;
    }

    public void setStatus(final InformatieobjectStatus status) {
        this.status = status.toString();
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

    public String getInhoudUrl() {
        return inhoudUrl;
    }

    public void setInhoudUrl(final String inhoudUrl) {
        this.inhoudUrl = inhoudUrl;
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

    public boolean isIndicatie(final DocumentIndicatie indicatie) {
        return this.indicaties != null && this.indicaties.contains(indicatie.name());
    }

    public EnumSet<DocumentIndicatie> getDocumentIndicaties() {
        if (this.indicaties == null) {
            return EnumSet.noneOf(DocumentIndicatie.class);
        }
        return this.indicaties.stream().map(DocumentIndicatie::valueOf)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(DocumentIndicatie.class)));
    }

    public void setIndicatie(final DocumentIndicatie indicatie, final Boolean value) {
        if (value != null) {
            updateIndicaties(indicatie, value);
            updateIndicatieVolgorde(indicatie, value);
        }
    }

    private void updateIndicaties(DocumentIndicatie indicatie, boolean value) {
        final String key = indicatie.name();
        if (this.indicaties == null) {
            this.indicaties = new ArrayList<>();
        }
        if (value) {
            if (!this.indicaties.contains(key)) {
                this.indicaties.add(key);
            }
        } else {
            this.indicaties.remove(key);
        }
    }

    private void updateIndicatieVolgorde(final DocumentIndicatie indicatie, boolean value) {
        final int bit = DocumentIndicatie.values().length - 1 - indicatie.ordinal();
        if (value) {
            this.indicatiesVolgorde |= 1L << bit;
        } else {
            this.indicatiesVolgorde &= ~(1L << bit);
        }
    }
}
