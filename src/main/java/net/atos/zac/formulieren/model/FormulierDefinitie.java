/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.formulieren.model;


import static net.atos.zac.util.FlywayIntegrator.SCHEMA;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.BooleanUtils;

@Entity
@Table(schema = SCHEMA, name = "formulier_definitie")
@SequenceGenerator(schema = SCHEMA, name = "sq_formulier_definitie", sequenceName = "sq_formulier_definitie", allocationSize = 1)
public class FormulierDefinitie {

    @Id
    @GeneratedValue(generator = "sq_formulier_definitie", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_formulier_definitie")
    private Long id;

    @NotBlank
    @Column(name = "systeemnaam", nullable = false, unique = true)
    private String systeemnaam;

    @NotBlank
    @Column(name = "naam", nullable = false)
    private String naam;

    @Column(name = "beschrijving")
    private String beschrijving;

    @Column(name = "uitleg")
    private String uitleg;

    @Column(name = "creatiedatum", nullable = false)
    private ZonedDateTime creatiedatum;

    @Column(name = "wijzigingsdatum", nullable = false)
    private ZonedDateTime wijzigingsdatum;

    @OneToMany(mappedBy = "formulierDefinitie", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<FormulierVeldDefinitie> veldDefinities;

    @Column(name = "mail_from")
    private String mailFrom;

    @Column(name = "mail_to")
    private String mailTo;

    @Column(name = "mail_subject")
    private String mailSubject;

    @Column(name = "mail_body")
    private String mailBody;

    @Column(name = "mail_versturen")
    private Boolean mailVersturen;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }


    public String getSysteemnaam() {
        return systeemnaam;
    }

    public void setSysteemnaam(final String systeemnaam) {
        this.systeemnaam = systeemnaam;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(final String naam) {
        this.naam = naam;
    }

    public String getBeschrijving() {
        return beschrijving;
    }

    public void setBeschrijving(final String beschrijving) {
        this.beschrijving = beschrijving;
    }

    public String getUitleg() {
        return uitleg;
    }

    public void setUitleg(final String uitleg) {
        this.uitleg = uitleg;
    }

    public ZonedDateTime getCreatiedatum() {
        return creatiedatum;
    }

    public void setCreatiedatum(final ZonedDateTime creatiedatum) {
        this.creatiedatum = creatiedatum;
    }

    public ZonedDateTime getWijzigingsdatum() {
        return wijzigingsdatum;
    }

    public void setWijzigingsdatum(final ZonedDateTime wijzigingsdatum) {
        this.wijzigingsdatum = wijzigingsdatum;
    }

    public Set<FormulierVeldDefinitie> getVeldDefinities() {
        return veldDefinities != null ? veldDefinities : Collections.emptySet();
    }

    public void setVeldDefinities(final Collection<FormulierVeldDefinitie> veldDefinities) {
        if (this.veldDefinities == null) {
            this.veldDefinities = new HashSet<>();
        } else {
            this.veldDefinities.clear();
        }
        veldDefinities.forEach(this::addVeldDefinitie);
    }

    private void addVeldDefinitie(final FormulierVeldDefinitie veldDefinitie) {
        veldDefinitie.setFormulierDefinitie(this);
        veldDefinities.add(veldDefinitie);
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(final String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public String getMailTo() {
        return mailTo;
    }

    public void setMailTo(final String mailTo) {
        this.mailTo = mailTo;
    }

    public String getMailSubject() {
        return mailSubject;
    }

    public void setMailSubject(final String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public String getMailBody() {
        return mailBody;
    }

    public void setMailBody(final String mailBody) {
        this.mailBody = mailBody;
    }

    public boolean isMailVersturen() {
        return BooleanUtils.isTrue(mailVersturen);
    }

    public void setMailVersturen(final boolean mailVersturen) {
        this.mailVersturen = mailVersturen;
    }
}

