/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {ZoekObject} from '../zoek-object';

export class ZaakZoekObject implements ZoekObject {
    identificatie: string;
    type: string;
    uuid: string;
}
