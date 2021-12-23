/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {PlanItemDefinitie} from './plan-item-definitie';

export class CaseModel {
    key: string;
    naam: string;
    planItemDefinities: PlanItemDefinitie[];
}
