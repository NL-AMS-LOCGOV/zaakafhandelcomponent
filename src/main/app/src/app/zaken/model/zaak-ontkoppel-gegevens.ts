/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {ZaakRelatietype} from './zaak-relatietype';

export class ZaakOntkoppelGegevens {
    teOntkoppelenZaakUUID: string;
    ontkoppelenVanZaakIdentificatie: string;
    zaakRelatietype: ZaakRelatietype;
    reden: string;
}
