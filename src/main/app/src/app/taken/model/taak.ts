/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {TaakStatus} from './taak-status.enum';
import {Group} from '../../identity/model/group';
import {User} from '../../identity/model/user';

export class Taak {
    id: string;
    naam: string;
    toelichting: string;
    creatiedatumTijd: string;
    toekenningsdatumTijd: string;
    streefdatum: string;
    behandelaar: User;
    groep: Group;
    zaakUUID: string;
    zaakIdentificatie: string;
    zaaktypeOmschrijving: string;
    status: TaakStatus;
    formulierDefinitie: string;
    taakdata: {};
}
