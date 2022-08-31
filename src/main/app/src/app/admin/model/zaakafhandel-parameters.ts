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
import {Resultaattype} from '../../zaken/model/resultaattype';

export class ZaakafhandelParameters {
    zaaktype: Zaaktype;
    caseDefinition: CaseDefinition;
    defaultBehandelaar: User;
    defaultGroep: Group;
    creatiedatum: string;
    einddatumGeplandWaarschuwing: number;
    uiterlijkeEinddatumAfdoeningWaarschuwing: number;
    zaakNietOntvankelijkResultaattype: Resultaattype;
    humanTaskParameters: HumanTaskParameter[];
    userEventListenerParameters: UserEventListenerParameter[];
    zaakbeeindigParameters: ZaakbeeindigParameter[];
    valide: boolean;
}
