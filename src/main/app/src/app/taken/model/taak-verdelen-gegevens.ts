/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {TaakToekennenGegevens} from './taak-toekennen-gegevens';

export class TaakVerdelenGegevens {

    taakGegevens: { taakId:string, zaakUuid:string }[];
    behandelaarGebruikersnaam: string;
    groepId: string;

}
