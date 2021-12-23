/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Groep} from '../../identity/model/groep';

export class PlanItemParameters {
    planItemDefinitionId: string;
    formulierDefinitieId: string;
    defaultGroep: Groep;
    streefdatum: string;
}
