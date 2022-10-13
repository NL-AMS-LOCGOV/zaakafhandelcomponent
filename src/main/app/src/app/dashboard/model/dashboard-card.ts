/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {SignaleringType} from '../../shared/signaleringen/signalering-type';
import {ObjectType} from './object-type';
import {DashboardCardType} from './dashboard-card-type';

export class DashboardCard {
    type: DashboardCardType;
    objectType: ObjectType;
    signaleringType?: SignaleringType;

    constructor(type: DashboardCardType, objectType: ObjectType, signaleringType?: SignaleringType) {
        this.type = type;
        this.objectType = objectType;
        this.signaleringType = signaleringType;
    }
}
