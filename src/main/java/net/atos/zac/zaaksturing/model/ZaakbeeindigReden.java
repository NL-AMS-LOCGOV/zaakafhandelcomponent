/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zaaksturing.model;

import static net.atos.zac.util.FlywayIntegrator.SCHEMA;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(schema = SCHEMA, name = "zaakbeeindigreden")
@SequenceGenerator(schema = SCHEMA, name = "sq_zaakbeeindigreden", sequenceName = "sq_zaakbeeindigreden", allocationSize = 1)
public class ZaakbeeindigReden {

    @Id
    @GeneratedValue(generator = "sq_zaakbeeindigreden", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_zaakbeeindigreden")
    private Long id;

    @NotBlank
    @Column(name = "naam", nullable = false)
    private String naam;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(final String naam) {
        this.naam = naam;
    }
}
