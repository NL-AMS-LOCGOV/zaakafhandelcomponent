/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.configuratie.model;

import static net.atos.zac.util.FlywayIntegrator.SCHEMA;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

/**
 * These are ISO 639-2/B language codes.
 * See https://en.wikipedia.org/wiki/List_of_ISO_639-2_codes (when there are two codes the * indicates the B-code)
 */
@Entity
@Table(schema = SCHEMA, name = "taal")
@SequenceGenerator(schema = SCHEMA, name = "sq_taal", sequenceName = "sq_taal", allocationSize = 1)
public class Taal {

    @Id
    @GeneratedValue(generator = "sq_taal", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_taal")
    private Long id;

    @NotBlank
    @Column(name = "code", nullable = false)
    private String code;

    @NotBlank
    @Column(name = "naam", nullable = false)
    private String naam;

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank
    @Column(name = "native", nullable = false)
    private String local;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(final String naam) {
        this.naam = naam;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(final String local) {
        this.local = local;
    }
}
