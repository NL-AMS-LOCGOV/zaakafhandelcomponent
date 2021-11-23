/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {ObjectType} from './object-type';
import {Opcode} from './opcode';

export class ScreenEvent {
    timestamp: number;
    opcode: Opcode;
    objectType: ObjectType;
    objectId: string;
    key: string;

    constructor(opcode: Opcode, objectType: ObjectType, objectId: string, timestamp?: number) {
        this.timestamp = timestamp;
        this.opcode = opcode;
        this.objectType = objectType;
        this.objectId = objectId;
        this.key = this.opcode + ';' + this.objectType + ';' + this.objectId;
    }
}
