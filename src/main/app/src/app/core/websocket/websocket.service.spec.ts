/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {WebsocketService} from './websocket.service';
import {Opcode} from './model/opcode';
import {ObjectType} from './model/object-type';
import {WebsocketListener} from './model/websocket-listener';

describe('WebsocketService', () => {
    let service: WebsocketService;
    let listeners: WebsocketListener[] = [];

    beforeEach(() => {
        // Gebruik de mock. N.B. daarmee kan ALLEEN de listeners-logica getest worden.
        WebsocketService.test = true;
        service = new WebsocketService(null, null);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    const EVENTS = 8192;
    const OPCODES = [
        Opcode.UPDATED,
        Opcode.DELETED];
    const OBJECT_TYPES = [
        ObjectType.ENKELVOUDIG_INFORMATIEOBJECT,
        ObjectType.TAAK,
        ObjectType.ZAAK,
        ObjectType.ZAAK_ROLLEN,
        ObjectType.ZAAK_INFORMATIEOBJECTEN,
        ObjectType.ZAAK_TAKEN];
    const MAX_DELAY = 256; // ms

    it('should dispatch events to the correct listeners', (done) => {
        for (var i = 0; i < EVENTS; i++) {
            var opcode: Opcode = OPCODES[Math.floor(Math.random() * OPCODES.length)];
            var objectType: ObjectType = OBJECT_TYPES[Math.floor(Math.random() * OBJECT_TYPES.length)];
            var delay: string = Math.floor(Math.random() * MAX_DELAY).toString();
            listeners.push(service.addListener(opcode, objectType, delay, callback(opcode, objectType, delay, done)));
        }
    });

    var received: number = 0;

    function callback(opcode: Opcode, objectType: ObjectType, objectId: string, done) {
        return event => {
            expect(typeof event.timestamp).toEqual('number');
            expect(event.opcode).toEqual(opcode);
            expect(event.objectType).toEqual(objectType);
            expect(event.objectId).toEqual(objectId);
            expect(event.key).toEqual(opcode + ';' + objectType + ';' + objectId);
            if (++received == EVENTS) {
                done();
                cleanup();
            }
        };
    }

    function cleanup() {
        for (var i = 0; i < listeners.length; i++) {
            service.removeListener(listeners[i]);
        }
        listeners.length = 0;
    }
});
