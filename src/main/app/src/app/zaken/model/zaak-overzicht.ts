/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {User} from '../../identity/model/user';
import {ZaakResultaat} from './zaak-resultaat';
import {ZaakActies} from '../../policy/model/zaak-acties';

export class ZaakOverzicht {
    identificatie: string;
    uuid: string;
    toelichting: string;
    omschrijving: string;
    startdatum: string;
    status: string;
    zaaktype: string;
    behandelaar: User;
    uiterlijkeDatumAfdoening: string;
    resultaat: ZaakResultaat;
    acties: ZaakActies;
}
