/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Zaak} from './zaak';

export class ZaakVerlengGegevens {
    zaak: Zaak;
    duurDagen: number;
    takenVerlengen: boolean;
}
