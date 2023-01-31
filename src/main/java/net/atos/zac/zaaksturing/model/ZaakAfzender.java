/*
 * SPDX-FileCopyrightText: 2023 Atos
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
@Table(schema = SCHEMA, name = "zaakafzender")
@SequenceGenerator(schema = SCHEMA, name = "sq_zaakafzender", sequenceName = "sq_zaakafzender", allocationSize = 1)
public class ZaakAfzender {

    public enum Speciaal {
        GEMEENTE,
        GROEP,
        BEHANDELAAR,
        MEDEWERKER;

        public boolean is(final String name) {
            return this.name().equals(name);
        }
    }

    @Id
    @GeneratedValue(generator = "sq_zaakafzender", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_zaakafzender")
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_zaakafhandelparameters", referencedColumnName = "id_zaakafhandelparameters")
    private ZaakafhandelParameters zaakafhandelParameters;

    @NotBlank
    @Column(name = "mail", nullable = false)
    private String mail;

    @Column(name = "default_mail", nullable = false)
    private boolean defaultMail;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public ZaakafhandelParameters getZaakafhandelParameters() {
        return zaakafhandelParameters;
    }

    public void setZaakafhandelParameters(final ZaakafhandelParameters zaakafhandelParameters) {
        this.zaakafhandelParameters = zaakafhandelParameters;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(final String mail) {
        this.mail = mail;
    }

    public boolean isDefault() {
        return defaultMail;
    }

    public void setDefault(final boolean defaultMail) {
        this.defaultMail = defaultMail;
    }
}
