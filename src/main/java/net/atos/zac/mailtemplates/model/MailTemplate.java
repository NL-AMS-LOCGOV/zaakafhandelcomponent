/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.mailtemplates.model;

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

import static net.atos.zac.util.FlywayIntegrator.SCHEMA;

@Entity
@Table(schema = SCHEMA, name = "mail_template")
@SequenceGenerator(schema = SCHEMA, name = "sq_mail_template", sequenceName = "sq_mail_template", allocationSize = 1)
public class MailTemplate {

    @Id
    @GeneratedValue(generator = "sq_mail_template", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_mail_template")
    private Long id;

    @NotBlank
    @Column(name = "mail_template_naam", nullable = false)
    private String mailTemplateNaam;

    @NotBlank
    @Column(name = "onderwerp", nullable = false)
    private String onderwerp;

    @NotBlank
    @Column(name = "body", nullable = false)
    private String body;

    @NotNull
    @Column(name = "mail_template_enum", nullable = false)
    @Enumerated(EnumType.STRING)
    private MailTemplateEnum mailTemplateEnum;

    @Column(name = "parent")
    private Long parent;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getMailTemplateNaam() {
        return mailTemplateNaam;
    }

    public void setMailTemplateNaam(final String mailTemplateNaam) {
        this.mailTemplateNaam = mailTemplateNaam;
    }

    public String getOnderwerp() {
        return onderwerp;
    }

    public void setOnderwerp(final String onderwerp) {
        this.onderwerp = onderwerp;
    }

    public String getBody() {
        return body;
    }

    public void setBody(final String body) {
        this.body = body;
    }

    public MailTemplateEnum getMailTemplateEnum() {
        return mailTemplateEnum;
    }

    public void setMailTemplateEnum(final MailTemplateEnum mailTemplateEnum) {
        this.mailTemplateEnum = mailTemplateEnum;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(final Long parent) {
        this.parent = parent;
    }
}
