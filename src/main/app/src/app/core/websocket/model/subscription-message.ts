/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {SubscriptionType} from './subscription-type';
import {ScreenUpdateEvent} from './screen-update-event';

export class SubscriptionMessage {
    subscriptionType: SubscriptionType;
    event: ScreenUpdateEvent;

    constructor(subscriptionType: SubscriptionType, event: ScreenUpdateEvent) {
        this.subscriptionType = subscriptionType;
        this.event = event;
    }
}
