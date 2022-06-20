/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {GerelateerdeZaak} from './gerelateerde-zaak';

export class ZaakKoppelGegevens {
    bronZaakUuid: string;
    gerelateerdeZaak: GerelateerdeZaak;
}
