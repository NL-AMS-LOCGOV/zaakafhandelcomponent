/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { PlanItemType } from "../../plan-items/model/plan-item-type.enum";

export class PlanItemDefinition {
  id: string;
  naam: string;
  type: PlanItemType;
  defaultFormulierDefinitie: string;
}
