/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { ScreenEvent } from "./screen-event";
import { EventCallback } from "./event-callback";

export class WebsocketListener {
  private static sequence = 0;
  private _id: string;
  private _event: ScreenEvent;
  private _callback: EventCallback;

  constructor(event: ScreenEvent, callback: EventCallback) {
    this._id = "lid" + WebsocketListener.sequence++;
    this._event = event;
    this._callback = callback;
  }

  get id(): string {
    return this._id;
  }

  get event(): ScreenEvent {
    return this._event;
  }

  callback(): void {
    // Warning: This no real incoming event! (it has no timestamp and it may even have an ANY opcode and/or objectType)
    this._callback(this._event);
  }

  get key(): string {
    return this._id + " (" + this._event.key + ")";
  }
}
