/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {PlanItemDefinition} from './plan-item-definition';
import {HumanTaskReferentieTabel} from './human-task-referentie-tabel';

export class HumanTaskParameter {
    planItemDefinition: PlanItemDefinition;
    defaultGroepId: string;
    doorlooptijd: number;
    referentieTabellen: HumanTaskReferentieTabel[];
}
