/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { WebsocketService } from "./websocket.service";
import { Opcode } from "./model/opcode";
import { ObjectType } from "./model/object-type";
import { WebsocketListener } from "./model/websocket-listener";

describe("WebsocketService", () => {
  const listeners: WebsocketListener[] = [];
  let service: WebsocketService;
  let received = 0;

  beforeEach(() => {
    // Gebruik de mock. N.B. daarmee kan ALLEEN de listeners-logica getest worden.
    WebsocketService.test = true;
    service = new WebsocketService(null, null);
  });
  it("should be created", () => {
    reset();
    expect(service).toBeTruthy();
  });

  it("should dispatch events to the correct listeners", (done) => {
    const EVENTS = 10000;
    const EXPECTED = EVENTS;
    reset();
    for (let i = 0; i < EVENTS; i++) {
      listeners.push(addRandomListener(EXPECTED, done));
    }
  });

  it("should not dispatch events to suspended listeners", (done) => {
    const EVENTS = 1000;
    const EXPECTED = EVENTS / 2;
    reset();
    for (let i = 0; i < EVENTS; i++) {
      const listener: WebsocketListener = addRandomListener(EXPECTED, done);
      listeners.push(listener);
      if (i % 2 === 0) {
        service.suspendListener(listener);
      }
    }
  });

  // Does not test ANY opcode and/or ANY objectType because the mock doesn't support it (see SessionRegistryTest for those)
  function addRandomListener(expected: number, done): WebsocketListener {
    const OPCODES = [Opcode.UPDATED, Opcode.DELETED];
    const OBJECT_TYPES = [
      ObjectType.ENKELVOUDIG_INFORMATIEOBJECT,
      ObjectType.TAAK,
      ObjectType.ZAAK,
      ObjectType.ZAAK_ROLLEN,
      ObjectType.ZAAK_INFORMATIEOBJECTEN,
      ObjectType.ZAAK_TAKEN,
    ];
    const MAX_DELAY = 512; // ms

    const opcode: Opcode = OPCODES[Math.floor(Math.random() * OPCODES.length)];
    const objectType: ObjectType =
      OBJECT_TYPES[Math.floor(Math.random() * OBJECT_TYPES.length)];
    const delay: string = Math.floor(Math.random() * MAX_DELAY).toString();
    return service.addListener(
      opcode,
      objectType,
      delay,
      callback(opcode, objectType, delay, expected, done),
    );
  }

  function callback(
    opcode: Opcode,
    objectType: ObjectType,
    objectId: string,
    expected: number,
    done,
  ) {
    return (event) => {
      expect(typeof event.timestamp).toEqual("number");
      expect(event.opcode).toEqual(opcode);
      expect(event.objectType).toEqual(objectType);
      expect(event.objectId).toEqual(objectId);
      expect(event.key).toEqual(opcode + ";" + objectType + ";" + objectId);
      if (++received === expected) {
        done();
        reset();
      }
    };
  }

  // This depends on listeners (do make sure to keep it consistent with what is registered with the service)
  function reset() {
    service.removeListeners(listeners);
    listeners.length = 0;
    received = 0;
  }
});
