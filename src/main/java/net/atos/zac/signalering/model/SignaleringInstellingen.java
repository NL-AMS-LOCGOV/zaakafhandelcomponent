/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.signalering.model;

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
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

@Entity
@Table(schema = SCHEMA, name = "signalering_instellingen")
@SequenceGenerator(schema = SCHEMA, name = "sq_signalering_instellingen", sequenceName = "sq_signalering_instellingen", allocationSize = 1)
public class SignaleringInstellingen {
    @Id
    @GeneratedValue(generator = "sq_signalering_instellingen", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_signalering_instellingen")
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "signaleringtype_enum", referencedColumnName = "signaleringtype_enum", nullable = false)
    private SignaleringType type;

    @Column(name = "id_groep", nullable = false)
    private String groep;

    @Column(name = "id_medewerker", nullable = false)
    private String medewerker;

    @Column(name = "dashboard")
    private boolean dashboard;

    @Column(name = "mail")
    private boolean mail;

    public SignaleringInstellingen() {
    }

    public SignaleringInstellingen(final SignaleringType type, final SignaleringTarget ownerType, final String ownerId) {
        this.type = type;
        switch (ownerType) {
            case GROUP -> {
                this.groep = ownerId;
            }
            case USER -> {
                this.medewerker = ownerId;
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public SignaleringType getType() {
        return type;
    }

    public void setType(final SignaleringType type) {
        this.type = type;
    }

    public String getGroep() {
        return groep;
    }

    public void setGroep(final String groep) {
        this.groep = groep;
    }

    public String getMedewerker() {
        return medewerker;
    }

    public void setMedewerker(final String medewerker) {
        this.medewerker = medewerker;
    }

    public String getOwner() {
        return medewerker != null ? medewerker : groep;
    }

    public boolean isDashboard() {
        return dashboard;
    }

    public void setDashboard(final boolean dashboard) {
        this.dashboard = dashboard;
    }

    public boolean isMail() {
        return mail;
    }

    public void setMail(final boolean mail) {
        this.mail = mail;
    }

    public boolean isEmpty() {
        return !dashboard && !mail;
    }

    @AssertTrue(message = "Of medewerker of groep moet ingevuld zijn (niet beide).")
    public boolean isValid() {
        return StringUtils.isNotBlank(medewerker) != StringUtils.isNotBlank((groep));
    }

    @Override
    public String toString() {
        return String.format("%s-signaleringinstellingen voor %s", getType(), getOwner());
    }
}
