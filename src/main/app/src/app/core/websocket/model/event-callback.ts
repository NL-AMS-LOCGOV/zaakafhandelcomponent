/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {ScreenUpdateEvent} from './screen-update-event';

export interface EventCallback {
    (event: ScreenUpdateEvent): void;
}
