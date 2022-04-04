/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {PlanItemType} from './plan-item-type.enum';
import {Groep} from '../../identity/model/groep';
import {Medewerker} from '../../identity/model/medewerker';
import {TaakStuurGegevens} from './taak-stuur-gegevens';

export class PlanItem {
    id: string;
    naam: string;
    type: PlanItemType;
    groep: Groep;
    medewerker: Medewerker;
    formulierDefinitie: string;
    zaakUuid: string;
    taakdata: {};
    taakStuurGegevens: TaakStuurGegevens;
}
