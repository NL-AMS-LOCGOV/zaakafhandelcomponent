/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {ScreenUpdateEvent} from './screen-update-event';

export class Listener {
    id: string;
    event: ScreenUpdateEvent;

    constructor(id: string, event: ScreenUpdateEvent) {
        this.id = id;
        this.event = event;
    }
}
