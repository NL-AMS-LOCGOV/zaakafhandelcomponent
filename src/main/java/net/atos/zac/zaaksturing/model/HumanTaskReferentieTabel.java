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
@Table(schema = SCHEMA, name = "humantask_referentie_tabel")
@SequenceGenerator(schema = SCHEMA, name = "sq_humantask_referentie_tabel", sequenceName = "sq_humantask_referentie_tabel", allocationSize = 1)
public class HumanTaskReferentieTabel {

    @Id
    @GeneratedValue(generator = "sq_humantask_referentie_tabel", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_humantask_referentie_tabel")
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_referentie_tabel", referencedColumnName = "id_referentie_tabel")
    private ReferentieTabel tabel;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_humantask_parameters", referencedColumnName = "id_humantask_parameters")
    private HumanTaskParameters humantask;

    @NotBlank
    @Column(name = "veld", nullable = false)
    private String veld;

    public HumanTaskReferentieTabel() {
    }

    public HumanTaskReferentieTabel(final String veld, final ReferentieTabel tabel) {
        this.veld = veld;
        this.tabel = tabel;
    }

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

    public HumanTaskParameters getHumantask() {
        return humantask;
    }

    public void setHumantask(final HumanTaskParameters humantask) {
        this.humantask = humantask;
    }

    public String getVeld() {
        return veld;
    }

    public void setVeld(final String veld) {
        this.veld = veld;
    }
}
