/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Zaaktype} from '../../zaken/model/zaaktype';
import {CaseModel} from './case-model';
import {PlanItemParameters} from './plan-item-parameters';
import {Groep} from '../../identity/model/groep';
import {Medewerker} from '../../identity/model/medewerker';

export class ZaakafhandelParameters {
    zaaktype: Zaaktype;
    caseModel: CaseModel;
    planItemParameters: PlanItemParameters[];
    defaultGroep: Groep;
    defaultBehandelaar: Medewerker;
}
