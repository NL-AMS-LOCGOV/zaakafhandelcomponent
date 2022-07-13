/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Group} from '../../identity/model/group';
import {PlanItemDefinition} from './plan-item-definition';

export class HumanTaskParameter {
    planItemDefinition: PlanItemDefinition;
    defaultGroep: Group;
    doorlooptijd: number;
}
