/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {SignaleringType} from '../../shared/signaleringen/signalering-type';

export class DashboardCardData {
    title: string;
    signaleringType: SignaleringType;
    objectType: 'ZAAK' | 'TAAK';

    constructor(title: string, signaleringType: SignaleringType, objectType: 'ZAAK' | 'TAAK') {
        this.title = title;
        this.signaleringType = signaleringType;
        this.objectType = objectType;
    }
}
