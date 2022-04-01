/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.documenten.model;

import static net.atos.zac.util.FlywayIntegrator.SCHEMA;

import java.time.ZonedDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(schema = SCHEMA, name = "ontkoppeld_document")
@SequenceGenerator(schema = SCHEMA, name = "sq_ontkoppeld_document", sequenceName = "sq_ontkoppeld_document", allocationSize = 1)
public class OntkoppeldDocument {

    @Id
    @GeneratedValue(generator = "sq_ontkoppeld_document", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_ontkoppeld_document")
    private Long id;

    @NotNull
    @Column(name = "uuid_document", nullable = false)
    private UUID documentUUID;

    @NotBlank
    @Column(name = "id_document", nullable = false)
    private String documentID;

    @NotBlank
    @Column(name = "id_zaak", nullable = false)
    private String zaakID;

    @NotNull
    @Column(name = "creatiedatum", nullable = false)
    private ZonedDateTime creatiedatum;

    @NotBlank
    @Column(name = "titel", nullable = false)
    private String titel;

    @Column(name = "bestandsnaam")
    private String bestandsnaam;

    @NotNull
    @Column(name = "ontkoppeld_op", nullable = false)
    private ZonedDateTime ontkoppeldOp;

    @NotBlank
    @Column(name = "id_ontkoppeld_door", nullable = false)
    private String ontkoppeldDoorID;

    @Column(name = "reden")
    private String reden;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public UUID getDocumentUUID() {
        return documentUUID;
    }

    public void setDocumentUUID(final UUID documentUUID) {
        this.documentUUID = documentUUID;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(final String documentID) {
        this.documentID = documentID;
    }

    public ZonedDateTime getCreatiedatum() {
        return creatiedatum;
    }

    public void setCreatiedatum(final ZonedDateTime creatiedatum) {
        this.creatiedatum = creatiedatum;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(final String titel) {
        this.titel = titel;
    }

    public String getBestandsnaam() {
        return bestandsnaam;
    }

    public void setBestandsnaam(final String bestandsnaam) {
        this.bestandsnaam = bestandsnaam;
    }

    public String getZaakID() {
        return zaakID;
    }

    public void setZaakID(final String zaakID) {
        this.zaakID = zaakID;
    }

    public ZonedDateTime getOntkoppeldOp() {
        return ontkoppeldOp;
    }

    public void setOntkoppeldOp(final ZonedDateTime ontkoppeldOp) {
        this.ontkoppeldOp = ontkoppeldOp;
    }

    public String getOntkoppeldDoorID() {
        return ontkoppeldDoorID;
    }

    public void setOntkoppeldDoorID(final String ontkoppeldDoorID) {
        this.ontkoppeldDoorID = ontkoppeldDoorID;
    }

    public String getReden() {
        return reden;
    }

    public void setReden(final String reden) {
        this.reden = reden;
    }
}

