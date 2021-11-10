/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {TaakStatus} from './taak-status.enum';
import {Groep} from '../../identity/model/groep';
import {Medewerker} from '../../identity/model/medewerker';

export class Taak {
    id: string;
    naam: string;
    toelichting: string;
    creatiedatumTijd: string;
    toekenningsdatumTijd: string;
    streefdatum: string;
    behandelaar: Medewerker;
    groep: Groep;
    zaakUUID: string;
    zaakIdentificatie: string;
    zaaktypeOmschrijving: string;
    status: TaakStatus;
    taakBehandelFormulier: string;
    taakdata: {};
    rechten: {};
}
