/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {BAGObject} from './bagobject';

export class Nummeraanduiding extends BAGObject {
    postcode: string;
    huisnummerWeergave: string;
    huisnummer: string;
    huisletter: string;
    huisnummertoevoeging: string;
    openbareRuimteNaam: string;
    woonplaatsNaam: string;
    woonplaats: string;
    status: 'UITGEGEVEN' | 'INGETROKKEN';
}
