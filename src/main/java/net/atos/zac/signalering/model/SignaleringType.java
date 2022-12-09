/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.signalering.model;

import static net.atos.zac.signalering.model.SignaleringTarget.GROUP;
import static net.atos.zac.signalering.model.SignaleringTarget.USER;
import static net.atos.zac.util.FlywayIntegrator.SCHEMA;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

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

    public static final String SEPARATOR = ";";

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
     * !! Don't forget to add a new enum value to signaleringtype table and to add the translation !!
     */
    public enum Type {
        ZAAK_DOCUMENT_TOEGEVOEGD("Zaakdocument toegevoegd", "Er is een document aan uw zaak toegevoegd.", USER),
        ZAAK_OP_NAAM("Zaak op naam", "Er is een zaak op uw naam gezet.", USER, GROUP),
        ZAAK_VERLOPEND("Zaak verloopt", "Uw zaak nadert de streefdatum." + SEPARATOR + "Uw zaak nadert de fatale datum.", USER),
        TAAK_OP_NAAM("Taak op naam", "Er is een taak op uw naam gezet.", USER),
        TAAK_VERLOPEN("Taak verloopt", "Uw taak heeft de fatale datum bereikt.", USER) {
            @Override
            public boolean isDashboard() {
                // This one has no dashboard card
                return false;
            }
        };

        private final String naam;

        private final String bericht;

        private final Set<SignaleringTarget> targets;

        Type(final String naam, final String bericht, SignaleringTarget... targets) {
            this.naam = naam;
            this.bericht = bericht;
            this.targets = Collections.unmodifiableSet(Arrays.stream(targets).collect(Collectors.toCollection(() -> EnumSet.noneOf(SignaleringTarget.class))));
        }

        public String getNaam() {
            return naam;
        }

        public String getBericht() {
            return bericht;
        }

        public boolean isTarget(final SignaleringTarget target) {return targets.contains(target);}

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
