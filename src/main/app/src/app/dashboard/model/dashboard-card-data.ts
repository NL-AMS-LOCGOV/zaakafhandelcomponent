/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {SignaleringType} from '../../shared/signaleringen/signalering-type';

export class DashboardCardData {
    objectType: 'INFORMATIEOBJECT' | 'TAAK' | 'ZAAK' | 'ZAAK-WAARSCHUWING';
    title: string;
    signaleringType?: SignaleringType;

    constructor(objectType: 'INFORMATIEOBJECT' | 'TAAK' | 'ZAAK' | 'ZAAK-WAARSCHUWING', title: string, signaleringType?: SignaleringType) {
        this.objectType = objectType;
        this.title = title;
        this.signaleringType = signaleringType;
    }
}
