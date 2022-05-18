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
@Table(schema = SCHEMA, name = "inbox_document")
@SequenceGenerator(schema = SCHEMA, name = "sq_inbox_document", sequenceName = "sq_inbox_document", allocationSize = 1)
public class InboxDocument {

    @Id
    @GeneratedValue(generator = "sq_inbox_document", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_inbox_document")
    private Long id;

    @NotNull
    @Column(name = "uuid_enkelvoudiginformatieobject", nullable = false)
    private UUID enkelvoudiginformatieobjectUUID;

    @NotBlank
    @Column(name = "id_enkelvoudiginformatieobject", nullable = false)
    private String enkelvoudiginformatieobjectID;

    @NotNull
    @Column(name = "creatiedatum", nullable = false)
    private ZonedDateTime creatiedatum;

    @NotBlank
    @Column(name = "titel", nullable = false)
    private String titel;

    @Column(name = "bestandsnaam")
    private String bestandsnaam;


    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }


    public UUID getEnkelvoudiginformatieobjectUUID() {
        return enkelvoudiginformatieobjectUUID;
    }

    public void setEnkelvoudiginformatieobjectUUID(final UUID enkelvoudiginformatieobjectUUID) {
        this.enkelvoudiginformatieobjectUUID = enkelvoudiginformatieobjectUUID;
    }

    public String getEnkelvoudiginformatieobjectID() {
        return enkelvoudiginformatieobjectID;
    }

    public void setEnkelvoudiginformatieobjectID(final String enkelvoudiginformatieobjectID) {
        this.enkelvoudiginformatieobjectID = enkelvoudiginformatieobjectID;
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
}
