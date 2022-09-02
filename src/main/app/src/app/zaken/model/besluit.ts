/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Besluittype} from './besluittype';

export class Besluit {
    url: string;
    identificatie: string;
    toelichting: string;
    datum: string;
    ingangsdatum: string;
    vervaldatum: string;
    besluittype: Besluittype;
}
