/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zaaksturing.model;

import static net.atos.zac.util.FlywayIntegrator.SCHEMA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;

@Entity
@Table(schema = SCHEMA, name = "referentie_tabel")
@SequenceGenerator(schema = SCHEMA, name = "sq_referentie_tabel", sequenceName = "sq_referentie_tabel", allocationSize = 1)
public class ReferentieTabel {
    public enum Systeem {
        ADVIES
    }

    @Id
    @GeneratedValue(generator = "sq_referentie_tabel", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_referentie_tabel")
    private Long id;

    @NotBlank
    @Column(name = "code", nullable = false)
    private String code;

    @NotBlank
    @Column(name = "naam", nullable = false)
    private String naam;

    @Transient
    private Boolean systeem;

    @OneToMany(mappedBy = "tabel", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<ReferentieTabelWaarde> waarden;

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
        if (!isSysteem()) {
            this.code = code;
        }
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(final String naam) {
        this.naam = naam;
    }

    public boolean isSysteem() {
        if (systeem == null) {
            systeem = Arrays.stream(Systeem.values())
                    .anyMatch(value -> value.name().equals(code));
        }
        return systeem;
    }

    public List<ReferentieTabelWaarde> getWaarden() {
        return waarden == null ? Collections.emptyList() : Collections.unmodifiableList(waarden);
    }

    public void setWaarden(final Collection<ReferentieTabelWaarde> waarden) {
        if (this.waarden == null) {
            this.waarden = new ArrayList<>();
        } else {
            this.waarden.clear();
        }
        waarden.forEach(this::addWaarde);
    }

    public void addWaarde(final ReferentieTabelWaarde waarde) {
        waarde.setTabel(this);
        waarden.add(waarde);
    }

    public void removeWaarde(final ReferentieTabelWaarde waarde) {
        waarden.remove(waarde);
    }
}
