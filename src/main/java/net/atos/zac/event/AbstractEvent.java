/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.event;

import java.io.Serializable;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import javax.json.bind.annotation.JsonbTransient;

public abstract class AbstractEvent<TYPE, ID> implements Serializable {

    private long timestamp;

    private Opcode opcode;

    private ID objectId;

    @JsonbTransient
    private int delay;

    /**
     * Constructor for the sake of JAXB
     */
    public AbstractEvent() {
        super();
    }

    public AbstractEvent(final Opcode opcode, final ID objectId) {
        this.timestamp = Instant.now().getEpochSecond();
        this.opcode = opcode;
        this.objectId = objectId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Opcode getOpcode() {
        return opcode;
    }

    public abstract TYPE getObjectType();

    public ID getObjectId() {
        return objectId;
    }

    public boolean delay() {
        if (0 < delay) {
            try {
                TimeUnit.SECONDS.sleep(delay);
            } catch (InterruptedException e) {
                // Ignore exception
            }
            return true;
        }
        return false;
    }

    public void setDelay(final int seconds) {
        this.delay = seconds;
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
        final AbstractEvent<?, ?> other = (AbstractEvent<?, ?>) obj;
        if (getOpcode() != other.getOpcode()) {
            return false;
        }
        if (!getObjectType().equals(other.getObjectType())) {
            return false;
        }
        return getObjectId().equals(other.getObjectId());
    }

    @Override
    public int hashCode() {
        int result = getOpcode().hashCode();
        result = 31 * result + getObjectType().hashCode();
        result = 31 * result + getObjectId().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s", getClass().getSimpleName(), getOpcode(), getObjectType(), getObjectId());
    }
}
