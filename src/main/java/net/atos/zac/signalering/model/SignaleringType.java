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
public class SignaleringType implements Comparable<SignaleringType> {

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
     * !! Remember to add the enum value to signaleringtype table and to add the translation !!
     */
    public enum Type {
        ZAAK_DOCUMENT_TOEGEVOEGD("Zaakdocument toegevoegd", "Er is een document aan uw zaak toegevoegd."),
        ZAAK_OP_NAAM("Zaak op naam", "Er is een zaak op uw naam gezet."),
        ZAAK_VERLOPEND("Zaak verloopt", "Uw zaak nadert de streef- of fatale datum."),
        TAAK_OP_NAAM("Taak op naam", "Er is een taak op uw naam gezet."),
        TAAK_VERLOPEN("Taak verloopt", "Uw taak heeft de streefdatum bereikt.") {
            @Override
            public boolean isDashboard() {
                // This one has no dashboard card
                return false;
            }
        };

        private final String naam;

        private final String bericht;

        Type(final String naam, final String bericht) {
            this.naam = naam;
            this.bericht = bericht;
        }

        public String getNaam() {
            return naam;
        }

        public String getBericht() {
            return bericht;
        }

        public boolean isDashboard() {
            return true;
        }

        public boolean isMail() {
            return true;
        }
    }

    @Override
    public int compareTo(final SignaleringType other) {
        final int result = this.getSubjecttype().compareTo(other.getSubjecttype());
        return result != 0 ? result : this.getType().compareTo(other.getType());
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
