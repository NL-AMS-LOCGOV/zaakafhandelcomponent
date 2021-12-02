/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.websocket.event;

import javax.json.bind.annotation.JsonbTransient;

import net.atos.zac.event.AbstractEvent;
import net.atos.zac.event.Opcode;

public class ScreenEvent extends AbstractEvent<ScreenEventType, String> {

    private static final long serialVersionUID = -740125186878024703L;

    private ScreenEventType objectType;

    @JsonbTransient
    private boolean delay;

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

    public boolean isDelay() {
        return delay;
    }

    public void setDelay(final boolean delay) {
        this.delay = delay;
    }
}
