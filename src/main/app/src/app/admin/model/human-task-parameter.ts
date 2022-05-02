/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Groep} from '../../identity/model/groep';
import {PlanItemDefinition} from './plan-item-definition';

export class HumanTaskParameter {
    planItemDefinition: PlanItemDefinition;
    formulierDefinitie: string;
    defaultGroep: Groep;
    doorlooptijd: number;
}
