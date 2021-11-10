/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.event;

/**
 * Enumeration containing the operations as used by the {@link AbstractUpdateEvent}.
 * Maps to opcode.ts
 */
public enum OpcodeEnum {

    /**
     * indication that the mentioned object has been added
     */
    CREATED,

    /**
     * indication that the mentioned object has been updated
     */
    UPDATED,

    /**
     * indication that the mentioned object has been deleted
     */
    DELETED
}
