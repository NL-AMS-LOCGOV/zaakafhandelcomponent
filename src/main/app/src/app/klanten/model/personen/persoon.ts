/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Klant} from '../klant';

export class Persoon implements Klant {
    bsn: string;
    geslacht: string;
    naam: string;
    geboortedatum: string;
    inschrijfadres: string;
    identificatie: string;
    klantType: string;
}


