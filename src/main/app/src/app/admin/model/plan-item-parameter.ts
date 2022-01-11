/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Groep} from '../../identity/model/groep';
import {PlanItemDefinition} from './plan-item-definition';
import {FormulierDefinitieVerwijzing} from './formulier-definitie-verwijzing';

export class PlanItemParameter {
    planItemDefinition: PlanItemDefinition;
    formulierDefinitie: FormulierDefinitieVerwijzing;
    defaultGroep: Groep;
    doorlooptijd: number;
}
