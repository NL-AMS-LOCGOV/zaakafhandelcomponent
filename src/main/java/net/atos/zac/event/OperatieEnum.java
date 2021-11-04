/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.event;

/**
 * Enumeratie die de operaties bevat zoals die gebruikt worden door het {@link AbstractUpdateEvent}.
 */
public enum OperatieEnum {

    /**
     * indicatie dat het genoemde object is gewijzigd
     */
    WIJZIGING,

    /**
     * indicatie dat het genoemde object is toegevoegd
     */
    TOEVOEGING,

    /**
     * indicatie dat het genoemde object is verwijderd
     */
    VERWIJDERING
}
