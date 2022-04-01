/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.signalering.model;

import static net.atos.zac.util.FlywayIntegrator.SCHEMA;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Entity
@Table(schema = SCHEMA, name = "signaleringtype")
public class SignaleringType {

    @Id
    @Column(name = "signaleringtype_enum", updatable = false, insertable = false)
    private String id;

    @NotNull
    @Column(name = "subjecttype_enum", updatable = false, insertable = false)
    @Enumerated(EnumType.STRING)
    private SignaleringSubject subjecttype;

    /* Workaround, Hibernate does not support primary keys of type enum. */
    @Transient
    private Type type;

    public Type getType() {
        if (type == null) {
            type = Type.valueOf(id);
        }
        return type;
    }

    public SignaleringSubject getSubjecttype() {
        return subjecttype;
    }

    /**
     * Maps to signalering-type.ts
     * !! Remember to add the enum value to signaleringtype table !!
     */
    public enum Type {
        ZAAK_DOCUMENT_TOEGEVOEGD,
        ZAAK_OP_NAAM,
        TAAK_OP_NAAM;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        SignaleringType signaleringType = (SignaleringType) other;
        return getType() == signaleringType.getType();
    }

    @Override
    public int hashCode() {
        return getType().hashCode();
    }

    @Override
    public String toString() {
        return getType().toString();
    }
}
