/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.signalering.model;

import static net.atos.zac.util.FlywayIntegrator.SCHEMA;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(schema = SCHEMA, name = "signalering_verzonden")
@SequenceGenerator(schema = SCHEMA, name = "sq_signalering_verzonden", sequenceName = "sq_signalering_verzonden", allocationSize = 1)
public class SignaleringVerzonden {
    @Id
    @GeneratedValue(generator = "sq_signalering_verzonden", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_signalering_verzonden")
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "signaleringtype_enum", referencedColumnName = "signaleringtype_enum", nullable = false)
    private SignaleringType type;

    @NotNull
    @Column(name = "targettype_enum", nullable = false)
    @Enumerated(EnumType.STRING)
    private SignaleringTarget targettype;

    @NotBlank
    @Column(name = "target", nullable = false)
    private String target;

    @NotBlank
    @Column(name = "subject", nullable = false)
    private String subject;

    @NotNull
    @Column(name = "subjectfield_enum", nullable = false)
    @Enumerated(EnumType.STRING)
    private SignaleringDetail subjectfield;

    @Column(name = "detail")
    private String detail;

    @NotNull
    @Column(name = "tijdstip", nullable = false)
    private ZonedDateTime tijdstip;

    public Long getId() {
        return id;
    }

    public SignaleringType getType() {
        return type;
    }

    public void setType(final SignaleringType type) {
        this.type = type;
    }

    public SignaleringTarget getTargettype() {
        return targettype;
    }

    public void setTargettype(final SignaleringTarget targettype) {
        this.targettype = targettype;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(final String target) {
        this.target = target;
    }

    public SignaleringSubject getSubjecttype() {
        return getType().getSubjecttype();
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(final String subject) {
        this.subject = subject;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(final String detail) {
        this.detail = detail;
    }

    public ZonedDateTime getTijdstip() {
        return tijdstip;
    }

    public void setTijdstip(final ZonedDateTime tijdstip) {
        this.tijdstip = tijdstip;
    }
}
