/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {ZoekObject} from '../zoek-object';

export class TaakZoekObject implements ZoekObject {
    uuid: string;
    type: string;
    identificatie: string;
    naam: string;
    toelichting: string;
    status: string;
    zaaktypeUuid: string;
    zaaktypeIdentificatie: string;
    zaaktypeOmschrijving: string;
    zaakUuid: string;
    zaakIdentificatie: string;
    creatiedatum: string;
    toekenningsdatum: string;
    streefdatum: string;
    groepID: string;
    groepNaam: string;
    behandelaarNaam: string;
    behandelaarGebruikersnaam: string;
    taakData: string[];
    taakInformatie: string[];
}
