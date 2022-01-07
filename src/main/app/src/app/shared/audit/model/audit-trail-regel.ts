/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Wijziging} from './wijziging';

export class AuditTrailRegel {
    uuid: number;
    applicatieId: string;
    applicatieWeergave: string;
    gebruikersId: string;
    gebruikersWeergave: string;
    actie: string;
    actieWeergave: string;
    httpStatusCode: number;
    resource: number;
    resourceID: string;
    resourceWeergave: string;
    wijzigingsDatumTijd: string;
    toelichting: string;
    wijziging: Wijziging;
}
