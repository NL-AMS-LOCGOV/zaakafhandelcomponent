/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zaaksturing.model;

import static net.atos.zac.util.FlywayIntegrator.SCHEMA;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import net.atos.zac.mailtemplates.model.MailTemplate;

@Entity
@Table(schema = SCHEMA, name = "mail_template_koppelingen")
@SequenceGenerator(schema = SCHEMA, name = "sq_mail_template_koppelingen",
        sequenceName = "sq_mail_template_koppelingen", allocationSize = 1)
public class MailtemplateKoppeling {

    @Id
    @GeneratedValue(generator = "sq_mail_template_koppelingen", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_mail_template_koppelingen")
    private Long id;

    @NotNull
    @Column(name = "uuid_zaaktype", nullable = false)
    private UUID zaaktypeUUID;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_mail_template", referencedColumnName = "id_mail_template")
    private MailTemplate mailTemplate;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public UUID getZaaktypeUUID() {
        return zaaktypeUUID;
    }

    public void setZaaktypeUUID(final UUID zaaktypeUUID) {
        this.zaaktypeUUID = zaaktypeUUID;
    }

    public MailTemplate getMailTemplate() {
        return mailTemplate;
    }

    public void setMailTemplate(final MailTemplate mailTemplate) {
        this.mailTemplate = mailTemplate;
    }
}
