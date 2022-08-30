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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(schema = SCHEMA, name = "referentie_waarde")
@SequenceGenerator(schema = SCHEMA, name = "sq_referentie_waarde", sequenceName = "sq_referentie_waarde", allocationSize = 1)
public class ReferentieTabelWaarde {

    @Id
    @GeneratedValue(generator = "sq_referentie_waarde", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_referentie_waarde")
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_referentie_tabel", referencedColumnName = "id_referentie_tabel")
    private ReferentieTabel tabel;

    @NotBlank
    @Column(name = "naam", nullable = false)
    private String naam;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public ReferentieTabel getTabel() {
        return tabel;
    }

    public void setTabel(final ReferentieTabel tabel) {
        this.tabel = tabel;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(final String naam) {
        this.naam = naam;
    }
}
