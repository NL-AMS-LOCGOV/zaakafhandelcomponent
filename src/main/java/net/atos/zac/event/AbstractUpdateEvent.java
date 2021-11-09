/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.event;

import java.io.Serializable;
import java.time.Instant;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import net.atos.zac.websocket.event.ScreenUpdateEvent;

public abstract class AbstractUpdateEvent<TYPE, ID> implements Serializable {

    private long timestamp;

    @NotNull
    private OpcodeEnum operation;

    @NotBlank
    @Valid
    private ID objectId;

    /**
     * Constructor for the sake of JAXB
     */
    public AbstractUpdateEvent() {
        super();
    }

    public AbstractUpdateEvent(final OpcodeEnum operation, final ID objectId) {
        this.timestamp = Instant.now().getEpochSecond();
        this.operation = operation;
        this.objectId = objectId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public OpcodeEnum getOperation() {
        return operation;
    }

    public abstract TYPE getObjectType();

    public ID getObjectId() {
        return objectId;
    }

    @Override
    public boolean equals(final Object obj) {
        // snel antwoord
        if (obj == this) {
            return true;
        }
        // gebruik getClass i.p.v. instanceof, maar dan wel met de null check
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        // cast en vergelijk
        final ScreenUpdateEvent other = (ScreenUpdateEvent) obj;
        if (getOperation() != other.getOperation()) {
            return false;
        }
        if (getObjectType() != other.getObjectType()) {
            return false;
        }
        return getObjectId().equals(other.getObjectId());
    }

    @Override
    public int hashCode() {
        int result = getOperation().hashCode();
        result = 31 * result + getObjectType().hashCode();
        result = 31 * result + getObjectId().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s", getClass().getSimpleName(), getOperation(), getObjectType(), getOperation());
    }
}
