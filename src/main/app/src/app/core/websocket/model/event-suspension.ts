/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export class EventSuspension {
  private _ttl: number;
  private _expires: number;
  private _count = 0;

  constructor(seconds: number) {
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
    return this._expires < new Date().getTime();
  }
}
