/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Zaaktype} from '../../zaken/model/zaaktype';
import {CaseDefinition} from './case-definition';
import {HumanTaskParameter} from './human-task-parameter';
import {ZaakbeeindigParameter} from './zaakbeeindig-parameter';
import {UserEventListenerParameter} from './user-event-listener-parameter';
import {Resultaattype} from '../../zaken/model/resultaattype';
import {ZaakStatusmailOptie} from '../../zaken/model/zaak-statusmail-optie';

export class ZaakafhandelParameters {
    zaaktype: Zaaktype;
    caseDefinition: CaseDefinition;
    defaultBehandelaarId: string;
    defaultGroepId: string;
    creatiedatum: string;
    einddatumGeplandWaarschuwing: number;
    uiterlijkeEinddatumAfdoeningWaarschuwing: number;
    zaakNietOntvankelijkResultaattype: Resultaattype;
    humanTaskParameters: HumanTaskParameter[];
    userEventListenerParameters: UserEventListenerParameter[];
    zaakbeeindigParameters: ZaakbeeindigParameter[];
    intakeMail: ZaakStatusmailOptie;
    afrondenMail: ZaakStatusmailOptie;
    productaanvraagtype: string;
    valide: boolean;
}
