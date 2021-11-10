/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {PlanItemType} from './plan-item-type.enum';
import {Groep} from '../../identity/model/groep';

export class PlanItem {
    id: string;
    naam: string;
    type: PlanItemType;
    groep: Groep;
    taakStartFormulier: string;
    taakdata: {};
}
