/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {PlanItemDefinition} from './plan-item-definition';

export class HumanTaskParameter {
    actief: boolean;
    planItemDefinition: PlanItemDefinition;
    startformulierDefinitieId: string;
    afhandelformulierDefinitieId: string;
    defaultGroepId: string;
    doorlooptijd: number;
}
