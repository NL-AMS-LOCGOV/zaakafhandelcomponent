/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.event;

/**
 * Enumeratie die de operaties bevat zoals die gebruikt worden door het {@link AbstractUpdateEvent}.
 */
public enum ActionEnum {

    /**
     * indicatie dat het genoemde object is gewijzigd
     */
    UPDATE,

    /**
     * indicatie dat het genoemde object is toegevoegd
     */
    CREATE,

    /**
     * indicatie dat het genoemde object is verwijderd
     */
    DELETE
}
