/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {ScreenEvent} from './screen-event';

export class EventSuspension {
    private _ttl: number;
    private _expires: number;
    private _count: number = 0;

    constructor(seconds: number = 5) {
        this._ttl = seconds * 1000;
        this.increment();
    }

    increment() {
        this._expires = new Date().getTime() + this._ttl;
        this._count++;
    }

    isDone(): boolean {
        return --this._count < 1;
    }

    isExpired(): boolean {
        return new Date().getTime() < this._expires;
    }
}
