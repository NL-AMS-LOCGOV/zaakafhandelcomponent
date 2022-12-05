/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.signalering.event;


import net.atos.zac.event.AbstractEvent;
import net.atos.zac.event.Opcode;
import net.atos.zac.identity.model.User;
import net.atos.zac.signalering.model.SignaleringType;

public class SignaleringEvent<ID> extends AbstractEvent<SignaleringType.Type, SignaleringEventId<ID>> {

    private static final long serialVersionUID = 184493471780916087L;

    private SignaleringType.Type objectType;

    private String actor;

    /**
     * Constructor for the sake of JAXB
     */
    public SignaleringEvent() {
        super();
    }

    /**
     * Constructor with all required fields.
     *
     * @param objectType the operation that happened on the referenced object (the opcode is always UPDATED for these events)
     * @param objectId   the identification of the object the operation was done on
     * @param actor      the user that initiated the operation directly or indirectly
     */
    public SignaleringEvent(final SignaleringType.Type objectType, final SignaleringEventId<ID> objectId,
            final User actor) {
        super(Opcode.UPDATED, objectId);
        this.objectType = objectType;
        this.actor = actor == null ? null : actor.getId();
    }

    @Override
    public SignaleringType.Type getObjectType() {
        return objectType;
    }

    public String getActor() {
        return actor;
    }

    @Override
    public String toString() {
        return String.format("%s %s", super.toString(), getActor());
    }
}
