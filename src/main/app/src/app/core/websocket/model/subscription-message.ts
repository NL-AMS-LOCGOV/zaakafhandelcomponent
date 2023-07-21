/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { SubscriptionType } from "./subscription-type";
import { ScreenEvent } from "./screen-event";

export class SubscriptionMessage {
  subscriptionType: SubscriptionType;
  event: ScreenEvent;

  constructor(subscriptionType: SubscriptionType, event: ScreenEvent) {
    this.subscriptionType = subscriptionType;
    this.event = event;
  }
}
