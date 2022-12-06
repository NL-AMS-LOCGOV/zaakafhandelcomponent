/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.websocket.event;

import net.atos.zac.event.AbstractEvent;
import net.atos.zac.event.Opcode;

public class ScreenEvent extends AbstractEvent<ScreenEventType, ScreenEventId> {

    private static final long serialVersionUID = -740125186878024703L;

    private ScreenEventType objectType;

    public ScreenEvent() {
        super();
    }

    public ScreenEvent(final Opcode opcode, final ScreenEventType objectType, final ScreenEventId objectId) {
        super(opcode, objectId);
        this.objectType = objectType;
    }

    @Override
    public ScreenEventType getObjectType() {
        return objectType;
    }
}
