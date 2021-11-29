/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {ObjectType} from './object-type';
import {Opcode} from './opcode';

export class ScreenEvent {
    private _timestamp: number;
    opcode: Opcode;
    objectType: ObjectType;
    objectId: string;
    private _key: string;

    constructor(opcode: Opcode, objectType: ObjectType, objectId: string, timestamp?: number) {
        this._timestamp = timestamp;
        this.opcode = opcode;
        this.objectType = objectType;
        this.objectId = objectId;
        this._key = this.opcode + ';' + this.objectType + ';' + this.objectId;
    }

    get timestamp(): number {
        return this._timestamp;
    }

    get key(): string {
        return this._key;
    }
}
