/*
 * SPDX-FileCopyrightText: 2022 Atos
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
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import net.atos.zac.signalering.model.SignaleringType;

@Entity
@Table(schema = SCHEMA, name = "dashboard_card")
@SequenceGenerator(schema = SCHEMA, name = "sq_dashboard_card", sequenceName = "sq_dashboard_card", allocationSize = 1)
public class DashboardCardInstelling {

    @Id
    @GeneratedValue(generator = "sq_dashboard_card", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_dashboard_card")
    private Long id;

    @NotBlank
    @Column(name = "id_medewerker", nullable = false)
    private String medewerkerId;

    @NotNull
    @Column(name = "dashboard_card_enum", nullable = false)
    @Enumerated(EnumType.STRING)
    private DashboardCardId cardId;

    @Transient
    private SignaleringType.Type signaleringType;

    @NotNull
    @Column(name = "kolom", nullable = false)
    private int kolom;

    @NotNull
    @Column(name = "volgorde", nullable = false)
    private int volgorde;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getMedewerkerId() {
        return medewerkerId;
    }

    public void setMedewerkerId(final String medewerkerID) {
        this.medewerkerId = medewerkerID;
    }

    public DashboardCardId getCardId() {
        return cardId;
    }

    public void setCardId(final DashboardCardId cardId) {
        this.cardId = cardId;
    }

    public SignaleringType.Type getSignaleringType() {
        return signaleringType;
    }

    public void setSignaleringType(final SignaleringType.Type signaleringType) {
        this.signaleringType = signaleringType;
    }

    public int getKolom() {
        return kolom;
    }

    public void setKolom(final int kolom) {
        this.kolom = kolom;
    }

    public int getVolgorde() {
        return volgorde;
    }

    public void setVolgorde(final int volgorde) {
        this.volgorde = volgorde;
    }
}
