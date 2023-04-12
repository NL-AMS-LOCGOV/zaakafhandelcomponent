/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken.model.zoekobject;

import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

import net.atos.zac.app.taken.model.TaakStatus;
import net.atos.zac.zoeken.model.ZoekObject;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

public class TaakZoekObject implements ZoekObject {

    public static final String BEHANDELAAR_ID_FIELD = "taak_behandelaarGebruikersnaam";

    @Field
    private String id;

    @Field
    private String type;

    @Field("taak_naam")
    private String naam;

    @Field("taak_toelichting")
    private String toelichting;

    @Field("taak_status")
    private String status;

    @Field("taak_zaaktypeUuid")
    private String zaaktypeUuid;

    @Field("taak_zaaktypeIdentificatie")
    private String zaaktypeIdentificatie;

    @Field("taak_zaaktypeOmschrijving")
    private String zaaktypeOmschrijving;

    @Field("taak_zaakUuid")
    private String zaakUUID;

    @Field("taak_zaakId")
    private String zaakIdentificatie;

    @Field("taak_creatiedatum")
    private Date creatiedatum;

    @Field("taak_toekenningsdatum")
    private Date toekenningsdatum;

    @Field("taak_fataledatum")
    private Date fataledatum;

    @Field("taak_groepId")
    private String groepID;

    @Field("taak_groepNaam")
    private String groepNaam;

    @Field("taak_behandelaarNaam")
    private String behandelaarNaam;

    @Field(BEHANDELAAR_ID_FIELD)
    private String behandelaarGebruikersnaam;

    @Field("taak_data")
    private List<String> taakData;

    @Field("taak_informatie")
    private List<String> taakInformatie;

    @Field(ZaakZoekObject.TOELICHTING_FIELD)
    private String zaakToelichting;

    @Field(ZaakZoekObject.OMSCHRIJVING_FIELD)
    private String zaakOmschrijving;

    @Field(IS_TOEGEKEND_FIELD)
    private boolean toegekend;


    public TaakZoekObject() {
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    @Override
    public String getObjectId() {
        return getId();
    }

    @Override
    public ZoekObjectType getType() {
        return ZoekObjectType.valueOf(type);
    }

    public void setType(final ZoekObjectType type) {
        this.type = type.toString();
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(final String naam) {
        this.naam = naam;
    }

    public String getToelichting() {
        return toelichting;
    }

    public void setToelichting(final String toelichting) {
        this.toelichting = toelichting;
    }

    public TaakStatus getStatus() {
        return TaakStatus.valueOf(status);
    }

    public void setStatus(final TaakStatus status) {
        this.status = status.toString();
    }

    public String getZaaktypeOmschrijving() {
        return zaaktypeOmschrijving;
    }

    public void setZaaktypeOmschrijving(final String zaaktypeOmschrijving) {
        this.zaaktypeOmschrijving = zaaktypeOmschrijving;
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

    public String getZaakUUID() {
        return zaakUUID;
    }

    public void setZaakUUID(final String zaakUUID) {
        this.zaakUUID = zaakUUID;
    }

    public String getZaakIdentificatie() {
        return zaakIdentificatie;
    }

    public void setZaakIdentificatie(final String zaakIdentificatie) {
        this.zaakIdentificatie = zaakIdentificatie;
    }

    public Date getCreatiedatum() {
        return creatiedatum;
    }

    public void setCreatiedatum(final Date creatiedatum) {
        this.creatiedatum = creatiedatum;
    }

    public Date getToekenningsdatum() {
        return toekenningsdatum;
    }

    public void setToekenningsdatum(final Date toekenningsdatum) {
        this.toekenningsdatum = toekenningsdatum;
    }

    public Date getFataledatum() {
        return fataledatum;
    }

    public void setFataledatum(final Date fataledatum) {
        this.fataledatum = fataledatum;
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

    public List<String> getTaakData() {
        return taakData;
    }

    public void setTaakData(final List<String> taakData) {
        this.taakData = taakData;
    }

    public List<String> getTaakInformatie() {
        return taakInformatie;
    }

    public void setTaakInformatie(final List<String> taakInformatie) {
        this.taakInformatie = taakInformatie;
    }

    public String getZaakToelichting() {
        return zaakToelichting;
    }

    public void setZaakToelichting(final String zaakToelichting) {
        this.zaakToelichting = zaakToelichting;
    }

    public String getZaakOmschrijving() {
        return zaakOmschrijving;
    }

    public void setZaakOmschrijving(final String zaakOmschrijving) {
        this.zaakOmschrijving = zaakOmschrijving;
    }

    public boolean isToegekend() {
        return toegekend;
    }

    public void setToegekend(final boolean toegekend) {
        this.toegekend = toegekend;
    }
}
