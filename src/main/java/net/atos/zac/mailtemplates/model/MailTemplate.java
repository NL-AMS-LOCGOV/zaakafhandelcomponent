/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.mailtemplates.model;

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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(schema = SCHEMA, name = "mail_template")
@SequenceGenerator(schema = SCHEMA, name = "sq_mail_template", sequenceName = "sq_mail_template", allocationSize = 1)
public class MailTemplate {

    /**
     * Naam van property: {@link MailTemplate#mail}
     */
    public static final String MAIL = "mail";

    /**
     * Naam van property: {@link MailTemplate#defaultMailtemplate}
     */
    public static final String DEFAULT_MAILTEMPLATE = "defaultMailtemplate";

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
    private Mail mail;

    @NotNull
    @Column(name = "default_mailtemplate", nullable = false)
    private Boolean defaultMailtemplate;

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

    public Mail getMail() {
        return mail;
    }

    public void setMail(final Mail mail) {
        this.mail = mail;
    }

    public Boolean isDefaultMailtemplate() {
        return defaultMailtemplate;
    }

    public void setDefaultMailtemplate(final Boolean defaultMailtemplate) {
        this.defaultMailtemplate = defaultMailtemplate;
    }
}
