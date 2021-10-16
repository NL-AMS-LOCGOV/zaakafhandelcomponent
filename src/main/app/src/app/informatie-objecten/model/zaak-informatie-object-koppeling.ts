/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {ZaakStatus} from '../../zaken/model/zaak-status';

export class ZaakInformatieObjectKoppeling {
    status: ZaakStatus;
    zaakUuid: string;
    zaakIdentificatie: string;
    zaaktype: string;
    zaakStartDatum: string;
    zaakEinddatumGepland: string;
}
