/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.event;

import java.util.EnumSet;
import java.util.Set;

/**
 * Enumeration containing the operations as used by the {@link AbstractEvent}.
 * Maps to opcode.ts
 */
public enum Opcode {

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
    DELETED,

    /**
     * indication that something has happened to the mentioned object
     */
    ANY;

    public static Set<Opcode> any() {
        return EnumSet.complementOf(EnumSet.of(ANY));
    }
}
