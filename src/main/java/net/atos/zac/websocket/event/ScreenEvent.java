/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.websocket.event;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

import net.atos.zac.event.AbstractEvent;
import net.atos.zac.event.Opcode;

public class ScreenEvent extends AbstractEvent<ScreenEventType, String> {

    private static final long serialVersionUID = -740125186878024703L;

    @NotNull
    private ScreenEventType objectType;

    public ScreenEvent() {
        super();
    }

    public ScreenEvent(final Opcode opcode, final ScreenEventType objectType, final String objectId) {
        super(opcode, objectId);
        this.objectType = objectType;
    }

    @Override
    public ScreenEventType getObjectType() {
        return objectType;
    }

    @AssertTrue(message = "Websocket subscriptions for CREATED objectIds cannot exist (the new id is unknown client side)")
    boolean isValid() {
        return getOperation() != Opcode.CREATED;
    }
}
