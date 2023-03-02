/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */
package net.atos.zac.gebruikersvoorkeuren.model;

import static net.atos.zac.util.FlywayIntegrator.SCHEMA;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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

    public static final int AANTAL_PER_PAGINA_DEFAULT = 25;

    public static final int AANTAL_PER_PAGINA_MAX = 100;

    public static final int AANTAL_PER_PAGINA_MIN = 10;

    public static final List<Integer> PAGE_SIZE_OPTIONS = List.of(AANTAL_PER_PAGINA_MIN, AANTAL_PER_PAGINA_DEFAULT, 50, AANTAL_PER_PAGINA_MAX);


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

    @Min(AANTAL_PER_PAGINA_MIN)
    @Max(AANTAL_PER_PAGINA_MAX)
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
