/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FormulierVeldType} from './formulier-veld-type.enum';

export class FormulierVeldDefinitie {
    id: number;
    systeemnaam: string;
    volgorde: number;
    label: string;
    type: FormulierVeldType;
    beschrijving: string;
    helptekst: string;
    verplicht: boolean;
    defaultWaarde: string;
    meerkeuzeWaarden: string[];
    validaties: string[];
}
