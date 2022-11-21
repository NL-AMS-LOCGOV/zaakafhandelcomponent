/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken.model.zoekobject;

import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

import net.atos.client.zgw.zrc.model.Rol;
import net.atos.zac.zoeken.model.ZaakIndicatie;
import net.atos.zac.zoeken.model.ZoekObject;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

public class ZaakZoekObject implements ZoekObject {

    public static final String AFGEHANDELD_FIELD = "zaak_afgehandeld";

    public static final String BEHANDELAAR_ID_FIELD = "zaak_behandelaarGebruikersnaam";

    public static final String EINDSTATUS_FIELD = "zaak_statusEindstatus";

    public static final String OMSCHRIJVING_FIELD = "zaak_omschrijving";

    public static final String TOELICHTING_FIELD = "zaak_toelichting";

    @Field
    private String id;

    @Field
    private String type;

    @Field("zaak_identificatie")
    private String identificatie;

    @Field(OMSCHRIJVING_FIELD)
    private String omschrijving;

    @Field(TOELICHTING_FIELD)
    private String toelichting;

    @Field("zaak_registratiedatum")
    private Date registratiedatum;

    @Field("zaak_startdatum")
    private Date startdatum;

    @Field("zaak_einddatumGepland")
    private Date einddatumGepland;

    @Field("zaak_einddatum")
    private Date einddatum;

    @Field("zaak_uiterlijkeEinddatumAfdoening")
    private Date uiterlijkeEinddatumAfdoening;

    @Field("zaak_publicatiedatum")
    private Date publicatiedatum;

    @Field("zaak_communicatiekanaal")
    private String communicatiekanaal;

    @Field("zaak_vertrouwelijkheidaanduiding")
    private String vertrouwelijkheidaanduiding;

    @Field(AFGEHANDELD_FIELD)
    private boolean afgehandeld;

    @Field("zaak_groepId")
    private String groepID;

    @Field("zaak_groepNaam")
    private String groepNaam;

    @Field("zaak_behandelaarNaam")
    private String behandelaarNaam;

    @Field(BEHANDELAAR_ID_FIELD)
    private String behandelaarGebruikersnaam;

    @Field("zaak_initiatorIdentificatie")
    private String initiatorIdentificatie;

    @Field("zaak_initiatorType")
    private String initiatorType;

    @Field("zaak_locatie")
    private String locatie;

    @Field("zaak_indicatieVerlenging")
    private boolean indicatieVerlenging;

    @Field("zaak_duurVerlenging")
    private String duurVerlenging;

    @Field("zaak_redenVerlenging")
    private String redenVerlenging;

    @Field("zaak_indicatieOpschorting")
    private boolean indicatieOpschorting;

    @Field("zaak_redenOpschorting")
    private String redenOpschorting;

    @Field("zaak_indicatieHeropend")
    private boolean indicatieHeropend;

    @Field("zaak_indicatieDeelzaak")
    private boolean indicatieDeelzaak;

    @Field("zaak_indicatieHoofdzaak")
    private boolean indicatieHoofdzaak;

    @Field("zaak_zaaktypeUuid")
    private String zaaktypeUuid;

    @Field("zaak_zaaktypeIdentificatie")
    private String zaaktypeIdentificatie;

    @Field("zaak_zaaktypeOmschrijving")
    private String zaaktypeOmschrijving;

    @Field("zaak_resultaattypeOmschrijving")
    private String resultaattypeOmschrijving;

    @Field("zaak_resultaatToelichting")
    private String resultaatToelichting;

    @Field(EINDSTATUS_FIELD)
    private boolean statusEindstatus;

    @Field("zaak_statustypeOmschrijving")
    private String statustypeOmschrijving;

    @Field("zaak_statusDatumGezet")
    private Date statusDatumGezet;

    @Field("zaak_statusToelichting")
    private String statusToelichting;

    @Field("zaak_aantalOpenstaandeTaken")
    private long aantalOpenstaandeTaken;

    @Field(IS_TOEGEKEND_FIELD)
    private boolean toegekend;

    @Field("zaak_indicaties_sort")
    private long indicaties;

    public ZaakZoekObject() {
    }

    public String getUuid() {
        return id;
    }

    public void setUuid(final String uuid) {
        this.id = uuid;
    }

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

    public Date getRegistratiedatum() {
        return registratiedatum;
    }

    public void setRegistratiedatum(final Date registratiedatum) {
        this.registratiedatum = registratiedatum;
    }

    public Date getStartdatum() {
        return startdatum;
    }

    public void setStartdatum(final Date startdatum) {
        this.startdatum = startdatum;
    }

    public Date getEinddatumGepland() {
        return einddatumGepland;
    }

    public void setEinddatumGepland(final Date einddatumGepland) {
        this.einddatumGepland = einddatumGepland;
    }

    public Date getEinddatum() {
        return einddatum;
    }

    public void setEinddatum(final Date einddatum) {
        this.einddatum = einddatum;
    }

    public Date getUiterlijkeEinddatumAfdoening() {
        return uiterlijkeEinddatumAfdoening;
    }

    public void setUiterlijkeEinddatumAfdoening(final Date uiterlijkeEinddatumAfdoening) {
        this.uiterlijkeEinddatumAfdoening = uiterlijkeEinddatumAfdoening;
    }

    public Date getPublicatiedatum() {
        return publicatiedatum;
    }

    public void setPublicatiedatum(final Date publicatiedatum) {
        this.publicatiedatum = publicatiedatum;
    }

    public void setStatusDatumGezet(final Date statusDatumGezet) {
        this.statusDatumGezet = statusDatumGezet;
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

    public void setInitiator(final Rol<?> initiator) {
        if (initiator != null) {
            this.initiatorIdentificatie = initiator.getIdentificatienummer();
            this.initiatorType = initiator.getBetrokkeneType().toValue();
        }
    }

    public String getLocatie() {
        return locatie;
    }

    public void setLocatie(final String locatie) {
        this.locatie = locatie;
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

    public String getResultaattypeOmschrijving() {
        return resultaattypeOmschrijving;
    }

    public void setResultaattypeOmschrijving(final String resultaattypeOmschrijving) {
        this.resultaattypeOmschrijving = resultaattypeOmschrijving;
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

    public String getStatustypeOmschrijving() {
        return statustypeOmschrijving;
    }

    public void setStatustypeOmschrijving(final String statustypeOmschrijving) {
        this.statustypeOmschrijving = statustypeOmschrijving;
    }

    public Date getStatusDatumGezet() {
        return statusDatumGezet;
    }

    public String getStatusToelichting() {
        return statusToelichting;
    }

    public void setStatusToelichting(final String statusToelichting) {
        this.statusToelichting = statusToelichting;
    }

    public long getAantalOpenstaandeTaken() {
        return aantalOpenstaandeTaken;
    }

    public void setAantalOpenstaandeTaken(final long aantalOpenstaandeTaken) {
        this.aantalOpenstaandeTaken = aantalOpenstaandeTaken;
    }

    public boolean isToegekend() {
        return toegekend;
    }

    public void setToegekend(final boolean toegekend) {
        this.toegekend = toegekend;
    }

    public boolean isIndicatie(final ZaakIndicatie indicatie) {
        return switch (indicatie) {
            case OPSCHORTING -> this.indicatieOpschorting;
            case HEROPEND -> this.indicatieHeropend;
            case HOOFDZAAK -> this.indicatieHoofdzaak;
            case DEELZAAK -> this.indicatieDeelzaak;
            case VERLENGD -> this.indicatieVerlenging;
        };
    }

    public void setIndicatie(final ZaakIndicatie indicatie, final boolean value) {
        switch (indicatie) {
            case OPSCHORTING -> this.indicatieOpschorting = value;
            case HEROPEND -> this.indicatieHeropend = value;
            case HOOFDZAAK -> this.indicatieHoofdzaak = value;
            case DEELZAAK -> this.indicatieDeelzaak = value;
            case VERLENGD -> this.indicatieVerlenging = value;
        }
        updateIndicaties(indicatie, value);
    }

    private void updateIndicaties(final ZaakIndicatie indicatie, boolean value) {
        final int bit = ZaakIndicatie.values().length - 1 - indicatie.ordinal();
        if (value) {
            this.indicaties |= 1L << bit;
        } else {
            this.indicaties &= ~(1L << bit);
        }
    }
}
