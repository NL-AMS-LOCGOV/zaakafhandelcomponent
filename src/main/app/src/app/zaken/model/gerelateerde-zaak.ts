/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {ZaakRelatietype} from './zaak-relatietype';

export class GerelateerdeZaak {
    identificatie: string;
    uuid: string;
    relatieType: ZaakRelatietype;
    omschrijving: string;
    toelichting: string;
    startdatum: string;
    einddatum: string;
}
