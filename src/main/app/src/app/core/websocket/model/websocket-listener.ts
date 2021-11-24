/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {ScreenEvent} from './screen-event';
import {EventCallback} from './event-callback';

export class WebsocketListener {
    private static sequence: number = 0;
    private _id: string;
    private _event: ScreenEvent;
    private _callback: EventCallback;

    constructor(event: ScreenEvent, callback: EventCallback) {
        this._id = 'lid' + (WebsocketListener.sequence++);
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
        this._callback(this._event); // This no real incoming event! (it has no timestamp)
    }
}
