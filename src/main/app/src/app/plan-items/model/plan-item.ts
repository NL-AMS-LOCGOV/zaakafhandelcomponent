/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {PlanItemType} from './plan-item-type.enum';
import {Group} from '../../identity/model/group';
import {User} from '../../identity/model/user';
import {TaakStuurGegevens} from './taak-stuur-gegevens';
import {UserEventListenerActie} from './user-event-listener-actie-enum';

export class PlanItem {
    id: string;
    naam: string;
    type: PlanItemType;
    groep: Group;
    medewerker: User;
    formulierDefinitie: string;
    zaakUuid: string;
    taakdata: {};
    userEventListenerActie: UserEventListenerActie;
    taakStuurGegevens: TaakStuurGegevens;
    toelichting: string;
}
