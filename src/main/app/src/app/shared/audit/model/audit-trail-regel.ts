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
    actie: string;
    actieWeergave: string;
    httpStatusCode: number;
    resource: number;
    resourceID: string;
    resourceWeergave: string;
    WijzigingsDatumTijd: string;
    toelichting: string;
    wijziging: Wijziging[];
}
