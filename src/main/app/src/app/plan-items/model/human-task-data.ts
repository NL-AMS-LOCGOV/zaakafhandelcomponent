/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Group} from '../../identity/model/group';
import {User} from '../../identity/model/user';
import {TaakStuurGegevens} from './taak-stuur-gegevens';
import {Moment} from 'moment/moment';

export class HumanTaskData {
    planItemInstanceId: string;
    groep: Group;
    medewerker: User;
    fataleDatum: Moment;
    taakdata: {};
    taakStuurGegevens: TaakStuurGegevens;
}
