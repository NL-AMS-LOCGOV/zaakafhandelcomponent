/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Zaaktype} from '../../zaken/model/zaaktype';
import {CaseDefinition} from './case-definition';
import {PlanItemParameters} from './plan-item-parameters';
import {Groep} from '../../identity/model/groep';
import {Medewerker} from '../../identity/model/medewerker';

export class ZaakafhandelParameters {
    zaaktype: Zaaktype;
    caseDefinition: CaseDefinition;
    planItemParameters: PlanItemParameters[];
    defaultGroep: Groep;
    defaultBehandelaar: Medewerker;
}
