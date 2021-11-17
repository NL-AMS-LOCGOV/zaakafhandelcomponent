/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Medewerker} from '../../identity/model/medewerker';
import {ZaakRechten} from './zaak-rechten';
import {ZaakResultaat} from './zaak-resultaat';

export class ZaakOverzicht {
    identificatie: string;
    uuid: string;
    toelichting: string;
    startdatum: string;
    status: string;
    zaaktype: string;
    aanvrager: string;
    behandelaar: Medewerker;
    uiterlijkeDatumAfdoening: string;
    rechten: ZaakRechten;
    resultaat: ZaakResultaat;
}
