/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.signalering.event;


import net.atos.zac.event.AbstractEvent;
import net.atos.zac.event.Opcode;
import net.atos.zac.signalering.model.SignaleringType;

public class SignaleringEvent<ID> extends AbstractEvent<SignaleringType.Type, ID> {

    private static final long serialVersionUID = 184493471780916087L;

    private SignaleringType.Type objectType;

    /**
     * Constructor for the sake of JAXB
     */
    public SignaleringEvent() {
        super();
    }

    /**
     * Constructor with all required fields.
     *
     * @param operation  the operation that happened on the referenced object
     * @param objectType the object type the operation was done on
     * @param objectId   the identification of the object the operation was done on
     */
    public SignaleringEvent(final Opcode operation, final SignaleringType.Type objectType, final ID objectId) {
        super(operation, objectId);
        this.objectType = objectType;
    }

    @Override
    public SignaleringType.Type getObjectType() {
        return objectType;
    }
}
