/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.event;

/**
 * Enumeratie die de operaties bevat zoals die gebruikt worden door het {@link AbstractUpdateEvent}.
 */
public enum OpcodeEnum {

    /**
     * indicatie dat het genoemde object is toegevoegd
     */
    CREATED,

    /**
     * indicatie dat het genoemde object is gewijzigd
     */
    UPDATED,

    /**
     * indicatie dat het genoemde object is verwijderd
     */
    DELETED
}
