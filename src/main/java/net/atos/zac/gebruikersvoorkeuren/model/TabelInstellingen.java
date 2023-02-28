/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */
package net.atos.zac.gebruikersvoorkeuren.model;

import static net.atos.zac.util.FlywayIntegrator.SCHEMA;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(schema = SCHEMA, name = "tabel_instellingen")
@SequenceGenerator(schema = SCHEMA, name = "sq_tabel_instellingen", sequenceName = "sq_tabel_instellingen", allocationSize = 1)
public class TabelInstellingen {

    /** Naam van property: {@link TabelInstellingen#medewerkerID} */
    public static final String MEDEWERKER_ID = "medewerkerID";

    /** Naam van property: {@link TabelInstellingen#lijstID} */
    public static final String LIJST_ID = "lijstID";

    public static final int DEFAULT_AANTAL_PER_PAGINA = 25;

    @Id
    @GeneratedValue(generator = "sq_tabel_instellingen", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_tabel_instellingen")
    private Long id;

    @NotNull
    @Column(name = "id_lijst_enum", nullable = false)
    @Enumerated(EnumType.STRING)
    private Werklijst lijstID;

    @NotBlank
    @Column(name = "id_medewerker", nullable = false)
    private String medewerkerID;

    @NotBlank
    @Column(name = "aantal_per_pagina", nullable = false)
    private int aantalPerPagina;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Werklijst getLijstID() {
        return lijstID;
    }

    public void setLijstID(final Werklijst lijstID) {
        this.lijstID = lijstID;
    }

    public String getMedewerkerID() {
        return medewerkerID;
    }

    public void setMedewerkerID(final String medewerkerID) {
        this.medewerkerID = medewerkerID;
    }

    public int getAantalPerPagina() {
        return aantalPerPagina;
    }

    public void setAantalPerPagina(final int aantalPerPagina) {
        this.aantalPerPagina = aantalPerPagina;
    }
}