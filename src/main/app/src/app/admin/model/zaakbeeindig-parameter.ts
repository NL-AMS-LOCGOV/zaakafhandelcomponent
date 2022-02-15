/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {ZaakbeeindigReden} from './zaakbeeindig-reden';
import {ZaakResultaat} from '../../zaken/model/zaak-resultaat';

export class ZaakbeeindigParameter {
    id: string;
    zaakbeeindigReden: ZaakbeeindigReden;
    zaakResultaat: ZaakResultaat;
}
