/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Zaaktype} from '../../zaken/model/zaaktype';
import {CaseDefinition} from './case-definition';
import {PlanItemParameter} from './plan-item-parameter';
import {Groep} from '../../identity/model/groep';
import {Medewerker} from '../../identity/model/medewerker';
import {ZaakbeeindigParameter} from './zaakbeeindig-parameter';

export class ZaakafhandelParameters {
    zaaktype: Zaaktype;
    caseDefinition: CaseDefinition;
    planItemParameters: PlanItemParameter[];
    zaakbeeindigParameters: ZaakbeeindigParameter[];
    defaultGroep: Groep;
    defaultBehandelaar: Medewerker;
}
