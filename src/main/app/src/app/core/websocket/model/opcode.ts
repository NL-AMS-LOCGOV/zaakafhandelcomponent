/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

/**
 * Maps to OpcodeEnum.java
 */
export enum Opcode {
    // CREATED = 'CREATED', Not available for subscription (new objectIds will be unknown client side ;-)
    UPDATED = 'UPDATED',
    DELETED = 'DELETED',
    ANY ='ANY'
}
