/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {ObjectType} from './object-type';
import {Opcode} from './opcode';

export class ScreenEvent {
    private _timestamp: number;
    private _opcode: Opcode;
    private _objectType: ObjectType;
    private _objectId: string;
    private _key: string;

    constructor(opcode: Opcode, objectType: ObjectType, objectId: string, timestamp?: number) {
        this._timestamp = timestamp;
        this._opcode = opcode;
        this._objectType = objectType;
        this._objectId = objectId;
        this._key = this._opcode + ';' + this._objectType + ';' + this._objectId;
    }

    get timestamp(): number {
        return this._timestamp;
    }

    get opcode(): Opcode {
        return this._opcode;
    }

    get objectType(): ObjectType {
        return this._objectType;
    }

    get objectId(): string {
        return this._objectId;
    }

    get key(): string {
        return this._key;
    }
}
