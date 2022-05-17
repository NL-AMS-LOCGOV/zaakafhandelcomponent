/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken.model.index;

import static net.atos.zac.util.FlywayIntegrator.SCHEMA;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


@Entity
@Table(schema = SCHEMA, name = "zoek_index")
@SequenceGenerator(schema = SCHEMA, name = "sq_zoek_index", sequenceName = "sq_zoek_index", allocationSize = 1)
public class ZoekIndexEntity {

    public static final String UUID = "uuid";

    @Id
    @GeneratedValue(generator = "sq_zoek_index", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_zoek_index")
    private Long id;

    @NotNull
    @Column(name = "uuid", nullable = false)
    private UUID uuid;

    @NotNull
    @Column(name = "type", nullable = false)
    private String type;

    @NotNull
    @Column(name = "status", nullable = false)
    private String status;

    public ZoekIndexEntity() {
        super();
    }

    public ZoekIndexEntity(final java.util.UUID uuid, final ZoekObjectType type, final IndexStatus status) {
        this.uuid = uuid;
        this.type = type.toString();
        this.status = status.toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public java.util.UUID getUuid() {
        return uuid;
    }

    public void setUuid(final java.util.UUID uuid) {
        this.uuid = uuid;
    }

    public String getType() {
        return type;
    }

    public void setType(final ZoekObjectType type) {
        this.type = type.toString();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final IndexStatus status) {
        this.status = status.toString();
    }
}
