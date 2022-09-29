/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {ZaakStatus} from '../../zaken/model/zaak-status';
import {ZaakActies} from '../../policy/model/zaak-acties';

export class ZaakInformatieobject {
    zaakIdentificatie: string;
    zaakStatus: ZaakStatus;
    zaakStartDatum: string;
    zaakEinddatumGepland: string;
    zaaktypeOmschrijving: string;
    zaakActies: ZaakActies;
}
