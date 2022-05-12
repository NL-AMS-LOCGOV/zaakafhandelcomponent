/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Zaaktype} from '../../zaken/model/zaaktype';
import {CaseDefinition} from './case-definition';
import {HumanTaskParameter} from './human-task-parameter';
import {Group} from '../../identity/model/group';
import {User} from '../../identity/model/user';
import {ZaakbeeindigParameter} from './zaakbeeindig-parameter';
import {UserEventListenerParameter} from './user-event-listener-parameter';

export class ZaakafhandelParameters {
    zaaktype: Zaaktype;
    caseDefinition: CaseDefinition;
    humanTaskParameters: HumanTaskParameter[];
    userEventListenerParameters: UserEventListenerParameter[];
    zaakbeeindigParameters: ZaakbeeindigParameter[];
    defaultGroep: Group;
    defaultBehandelaar: User;
    einddatumGeplandWaarschuwing: number;
    uiterlijkeEinddatumAfdoeningWaarschuwing: number;
}
