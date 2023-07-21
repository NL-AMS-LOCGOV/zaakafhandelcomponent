/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { SignaleringType } from "../../shared/signaleringen/signalering-type";
import { DashboardCardId } from "./dashboard-card-id";

export class DashboardCardInstelling {
  id: number;
  cardId: DashboardCardId;
  signaleringType?: SignaleringType;
  column: number;
  row: number;
}
