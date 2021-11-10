/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {WebsocketService} from './websocket.service';
import {Operatie} from './model/operatie';
import {ObjectType} from './model/object-type';

describe('WebsocketService', () => {
    let service: WebsocketService;

    beforeEach(() => {
        // Gebruik de mock. N.B. daarmee kan ALLEEN de listeners-logica getest worden.
        WebsocketService.test = true;
        service = new WebsocketService(null, null);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    const EVENTS = 8192;
    const OPERATIES = [
        Operatie.TOEVOEGING,
        Operatie.WIJZIGING,
        Operatie.VERWIJDERING];
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
            var operatie: Operatie = OPERATIES[Math.floor(Math.random() * OPERATIES.length)];
            var objectType: ObjectType = OBJECT_TYPES[Math.floor(Math.random() * OBJECT_TYPES.length)];
            var delay: string = Math.floor(Math.random() * MAX_DELAY).toString();
            service.addListener(operatie, objectType, delay, callback(operatie, objectType, delay, done));
        }
    });

    var received: number = 0;

    function callback(operatie: Operatie, objectType: ObjectType, objectId: string, done) {
        return event => {
            expect(typeof event.timestamp).toEqual('number');
            expect(event.operatie).toEqual(operatie);
            expect(event.objectType).toEqual(objectType);
            expect(event.objectId).toEqual(objectId);
            expect(event.key).toEqual(operatie + ';' + objectType + ';' + objectId);
            if (++received == EVENTS) {
                done();
                cleanup();
            }
        };
    }

    function cleanup() {
        for (var i = 0; i < MAX_DELAY; i++) {
            for (const operatie in Operatie) {
                for (const objectType in ObjectType) {
                    service.removeListeners(Operatie[operatie], ObjectType[objectType], i.toString());
                }
            }
        }
    }
});
