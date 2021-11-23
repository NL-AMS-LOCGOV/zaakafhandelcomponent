/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {ScreenEvent} from './screen-event';

export class WebsocketListener {
    private static sequence: number = 0;
    id: string;
    event: ScreenEvent;

    constructor(event: ScreenEvent) {
        this.id = 'lid' + (WebsocketListener.sequence++);
        this.event = event;
    }
}
