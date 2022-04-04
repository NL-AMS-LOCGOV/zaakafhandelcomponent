/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Medewerker} from '../../identity/model/medewerker';

export class OntkoppeldDocument {
    id: number;
    documentUUID: string;
    documentID: string;
    zaakID: string;
    creatiedatum: string;
    titel: string;
    bestandsnaam: string;
    ontkoppeldDoor: Medewerker;
    ontkoppeldOp: string;
    reden: string;
}
