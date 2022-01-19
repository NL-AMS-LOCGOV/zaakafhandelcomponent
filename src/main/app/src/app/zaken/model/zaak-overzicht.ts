/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Medewerker} from '../../identity/model/medewerker';
import {ZaakResultaat} from './zaak-resultaat';

export class ZaakOverzicht {
    identificatie: string;
    uuid: string;
    toelichting: string;
    omschrijving: string;
    startdatum: string;
    status: string;
    zaaktype: string;
    aanvrager: string;
    behandelaar: Medewerker;
    uiterlijkeDatumAfdoening: string;
    resultaat: ZaakResultaat;
}
