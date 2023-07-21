/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { SignaleringType } from "../../shared/signaleringen/signalering-type";
import { DashboardCardType } from "./dashboard-card-type";
import { DashboardCardId } from "./dashboard-card-id";

export class DashboardCard {
  readonly id: DashboardCardId;
  readonly type: DashboardCardType;
  readonly signaleringType?: SignaleringType;

  constructor(
    id: DashboardCardId,
    type: DashboardCardType,
    signaleringType?: SignaleringType,
  ) {
    this.id = id;
    this.type = type;
    this.signaleringType = signaleringType;
  }
}
