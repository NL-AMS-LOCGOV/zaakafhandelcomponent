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
        this._key = this.makeKey(this.opcode, this.objectType, this.objectId);
    }

    get timestamp(): number {
        return this._timestamp;
    }

    get key(): string {
        return this._key;
    }

    get keyAnyOpcode(): string {
        return this.makeKey(Opcode.ANY, this.objectType, this.objectId);
    }

    get keyAnyObjectType(): string {
        return this.makeKey(this.opcode, ObjectType.ANY, this.objectId);
    }

    get keyAnyOpcodeAndObjectType(): string {
        return this.makeKey(Opcode.ANY, ObjectType.ANY, this.objectId);
    }

    private makeKey(opcode: Opcode, objectType: ObjectType, objectId: string): string {
        return opcode + ';' + objectType + ';' + objectId;
    }
}
