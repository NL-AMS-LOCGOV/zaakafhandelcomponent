/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken.model;

import java.util.UUID;

import org.apache.solr.client.solrj.beans.Field;

public class ZoekZaakResultaat {

    @Field
    private UUID uuid;

    @Field
    private String identificatie;

    @Field
    private String zaaktype;

    @Field
    private String status;

    @Field
    private String omschrijving;

    @Field
    private String toelichting;

    @Field
    private String locatie;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(final UUID uuid) {
        this.uuid = uuid;
    }

    public String getIdentificatie() {
        return identificatie;
    }

    public void setIdentificatie(final String identificatie) {
        this.identificatie = identificatie;
    }

    public String getZaaktype() {
        return zaaktype;
    }

    public void setZaaktype(final String zaaktype) {
        this.zaaktype = zaaktype;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
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

    public String getLocatie() {
        return locatie;
    }

    public void setLocatie(final String locatie) {
        this.locatie = locatie;
    }
}
