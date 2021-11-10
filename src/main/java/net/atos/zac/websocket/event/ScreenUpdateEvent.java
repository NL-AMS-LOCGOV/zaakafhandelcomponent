/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.websocket.event;

import javax.validation.constraints.NotNull;

import net.atos.zac.event.AbstractUpdateEvent;
import net.atos.zac.event.OpcodeEnum;

public class ScreenUpdateEvent extends AbstractUpdateEvent<ScreenObjectTypeEnum, String> {

    private static final long serialVersionUID = -740125186878024703L;

    @NotNull
    private ScreenObjectTypeEnum objectType;

    public ScreenUpdateEvent() {
        super();
    }

    public ScreenUpdateEvent(final OpcodeEnum opcode, final ScreenObjectTypeEnum objectType, final String objectId) {
        super(opcode, objectId);
        this.objectType = objectType;
    }

    @Override
    public ScreenObjectTypeEnum getObjectType() {
        return objectType;
    }
}
