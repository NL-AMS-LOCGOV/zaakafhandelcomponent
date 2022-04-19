/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {SignaleringType} from '../../shared/signaleringen/signalering-type';

export class DashboardCardData {
    objectType: 'ZAAK' | 'TAAK' | 'INFORMATIEOBJECT';
    signaleringType: SignaleringType;
    title: string;

    constructor(objectType: 'ZAAK' | 'TAAK' | 'INFORMATIEOBJECT', signaleringType: SignaleringType, title: string) {
        this.objectType = objectType;
        this.signaleringType = signaleringType;
        this.title = title;
    }
}
