/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {ObjectType} from './object-type';
import {Operatie} from './operatie';

export class ScreenUpdateEvent {
    timestamp: number;
    operatie: Operatie;
    objectType: ObjectType;
    objectId: string;
    key: string;

    constructor(operatie: Operatie, objectType: ObjectType, objectId: string, timestamp?: number) {
        this.timestamp = timestamp;
        this.operatie = operatie;
        this.objectType = objectType;
        this.objectId = objectId;
        this.key = this.operatie + ';' + this.objectType + ';' + this.objectId;
    }
}
