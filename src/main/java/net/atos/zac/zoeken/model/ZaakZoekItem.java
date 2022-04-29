/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken.model;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.apache.solr.client.solrj.beans.Field;

public class ZaakZoekItem {

    @Field
    private String uuid;

    @Field
    private String type;

    @Field
    private String identificatie;

    @Field("zaak_omschrijving")
    private String omschrijving;

    @Field("zaak_toelichting")
    private String toelichting;

    @Field("zaak_registratiedatum")
    private String registratiedatum;

    @Field("zaak_startdatum")
    private String startdatum;

    @Field("zaak_streefdatum")
    private String streefdatum;

    @Field("zaak_einddatum")
    private String einddatum;

    @Field("zaak_fataledatum")
    private String fataledatum;

    @Field("zaak_publicatiedatum")
    private String publicatiedatum;

    @Field("zaak_communicatiekanaal")
    private String communicatiekanaal;

    @Field("zaak_vertrouwelijkheidaanduiding")
    private String vertrouwelijkheidaanduiding;

    @Field("zaak_afgehandeld")
    private boolean afgehandeld;

    @Field("zaak_groepId")
    private String groepID;

    @Field("zaak_groepNaam")
    private String groepNaam;

    @Field("zaak_behandelaarNaam")
    private String behandelaarNaam;

    @Field("zaak_behandelaarGebruikersnaam")
    private String behandelaarGebruikersnaam;

    @Field("zaak_initiatorIdentificatie")
    private String initiatorIdentificatie;

    @Field("zaak_locatie")
    private String locatie;

    @Field("zaak_indicatieVerlenging")
    private boolean IndicatieVerlenging;

    @Field("zaak_duurVerlenging")
    private String duurVerlenging;

    @Field("zaak_redenVerlenging")
    private String redenVerlenging;

    @Field("zaak_redenOpschorting")
    private String redenOpschorting;

    @Field("zaak_zaaktypeUuid")
    private String zaaktypeUuid;

    @Field("zaak_zaaktypeIdentificatie")
    private String zaaktypeIdentificatie;

    @Field("zaak_zaaktypeOmschrijving")
    private String zaaktypeOmschrijving;

    @Field("zaak_resultaatNaam")
    private String resultaatNaam;

    @Field("zaak_resultaatToelichting")
    private String resultaatToelichting;

    @Field("zaak_statusEindstatus")
    private boolean statusEindstatus;

    @Field("zaak_statusNaam")
    private String statusNaam;

    @Field("zaak_statusToekenningsdatum")
    private String statusToekenningsdatum;

    @Field("zaak_statusToelichting")
    private String statusToelichting;


    public ZaakZoekItem() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
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

    public String getToelichting() {
        return toelichting;
    }

    public void setToelichting(final String toelichting) {
        this.toelichting = toelichting;
    }

    public String getRegistratiedatum() {
        return registratiedatum;
    }

    public void setRegistratiedatum(final String registratiedatum) {
        this.registratiedatum = registratiedatum;
    }

    public String getStartdatum() {
        return startdatum;
    }

    public void setStartdatum(final String startdatum) {
        this.startdatum = startdatum;
    }

    public String getStreefdatum() {
        return streefdatum;
    }

    public void setStreefdatum(final String streefdatum) {
        this.streefdatum = streefdatum;
    }

    public String getEinddatum() {
        return einddatum;
    }

    public void setEinddatum(final String einddatum) {
        this.einddatum = einddatum;
    }

    public String getFataledatum() {
        return fataledatum;
    }

    public void setFataledatum(final String fataledatum) {
        this.fataledatum = fataledatum;
    }

    public String getPublicatiedatum() {
        return publicatiedatum;
    }

    public void setPublicatiedatum(final String publicatiedatum) {
        this.publicatiedatum = publicatiedatum;
    }

    public String getCommunicatiekanaal() {
        return communicatiekanaal;
    }

    public void setCommunicatiekanaal(final String communicatiekanaal) {
        this.communicatiekanaal = communicatiekanaal;
    }

    public String getVertrouwelijkheidaanduiding() {
        return vertrouwelijkheidaanduiding;
    }

    public void setVertrouwelijkheidaanduiding(final String vertrouwelijkheidaanduiding) {
        this.vertrouwelijkheidaanduiding = vertrouwelijkheidaanduiding;
    }

    public boolean isAfgehandeld() {
        return afgehandeld;
    }

    public void setAfgehandeld(final boolean afgehandeld) {
        this.afgehandeld = afgehandeld;
    }

    public String getGroepID() {
        return groepID;
    }

    public void setGroepID(final String groepID) {
        this.groepID = groepID;
    }

    public String getGroepNaam() {
        return groepNaam;
    }

    public void setGroepNaam(final String groepNaam) {
        this.groepNaam = groepNaam;
    }

    public String getBehandelaarNaam() {
        return behandelaarNaam;
    }

    public void setBehandelaarNaam(final String behandelaarNaam) {
        this.behandelaarNaam = behandelaarNaam;
    }

    public String getBehandelaarGebruikersnaam() {
        return behandelaarGebruikersnaam;
    }

    public void setBehandelaarGebruikersnaam(final String behandelaarGebruikersnaam) {
        this.behandelaarGebruikersnaam = behandelaarGebruikersnaam;
    }

    public String getInitiatorIdentificatie() {
        return initiatorIdentificatie;
    }

    public void setInitiatorIdentificatie(final String initiatorIdentificatie) {
        this.initiatorIdentificatie = initiatorIdentificatie;
    }

    public String getLocatie() {
        return locatie;
    }

    public void setLocatie(final String locatie) {
        this.locatie = locatie;
    }

    public boolean isIndicatieVerlenging() {
        return IndicatieVerlenging;
    }

    public void setIndicatieVerlenging(final boolean indicatieVerlenging) {
        IndicatieVerlenging = indicatieVerlenging;
    }

    public String getDuurVerlenging() {
        return duurVerlenging;
    }

    public void setDuurVerlenging(final String duurVerlenging) {
        this.duurVerlenging = duurVerlenging;
    }

    public String getRedenVerlenging() {
        return redenVerlenging;
    }

    public void setRedenVerlenging(final String redenVerlenging) {
        this.redenVerlenging = redenVerlenging;
    }

    public String getRedenOpschorting() {
        return redenOpschorting;
    }

    public void setRedenOpschorting(final String redenOpschorting) {
        this.redenOpschorting = redenOpschorting;
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

    public String getResultaatNaam() {
        return resultaatNaam;
    }

    public void setResultaatNaam(final String resultaatNaam) {
        this.resultaatNaam = resultaatNaam;
    }

    public String getResultaatToelichting() {
        return resultaatToelichting;
    }

    public void setResultaatToelichting(final String resultaatToelichting) {
        this.resultaatToelichting = resultaatToelichting;
    }

    public boolean isStatusEindstatus() {
        return statusEindstatus;
    }

    public void setStatusEindstatus(final boolean statusEindstatus) {
        this.statusEindstatus = statusEindstatus;
    }

    public String getStatusNaam() {
        return statusNaam;
    }

    public void setStatusNaam(final String statusNaam) {
        this.statusNaam = statusNaam;
    }

    public String getStatusToekenningsdatum() {
        return statusToekenningsdatum;
    }

    public void setStatusToekenningsdatum(final String statusToekenningsdatum) {
        this.statusToekenningsdatum = statusToekenningsdatum;
    }

    public String getStatusToelichting() {
        return statusToelichting;
    }

    public void setStatusToelichting(final String statusToelichting) {
        this.statusToelichting = statusToelichting;
    }
}
