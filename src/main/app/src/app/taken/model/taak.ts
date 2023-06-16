/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {TaakStatus} from './taak-status.enum';
import {Group} from '../../identity/model/group';
import {User} from '../../identity/model/user';
import {Taakinformatie} from './taakinformatie';
import {TaakRechten} from '../../policy/model/taak-rechten';
import {FormulierDefinitieID} from '../../admin/model/formulier-definitie';
import {FormulierDefinitie} from '../../admin/model/formulieren/formulier-definitie';

export class Taak {
    id: string;
    naam: string;
    toelichting: string;
    creatiedatumTijd: string;
    toekenningsdatumTijd: string;
    fataledatum: string;
    behandelaar: User;
    groep: Group;
    zaakUuid: string;
    zaakIdentificatie: string;
    zaaktypeOmschrijving: string;
    status: TaakStatus;
    formulierDefinitieId: FormulierDefinitieID;
    formulierDefinitie: FormulierDefinitie;
    tabellen: { [key: string]: string[] };
    taakdata: {};
    taakinformatie: Taakinformatie;
    taakdocumenten: string[];
    rechten: TaakRechten;
}
