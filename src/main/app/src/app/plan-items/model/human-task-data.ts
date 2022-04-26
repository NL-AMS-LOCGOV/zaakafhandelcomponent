/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Groep} from '../../identity/model/groep';
import {Medewerker} from '../../identity/model/medewerker';
import {TaakStuurGegevens} from './taak-stuur-gegevens';

export class HumanTaskData {
    planItemInstanceId: string;
    groep: Groep;
    medewerker: Medewerker;
    taakdata: {};
    taakStuurGegevens: TaakStuurGegevens;
}
