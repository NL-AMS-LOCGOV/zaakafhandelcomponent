/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { ZaakRelatietype } from "./zaak-relatietype";

export class ZaakKoppelGegevens {
  zaakUuid: string;
  teKoppelenZaakUuid: string;
  relatieType: ZaakRelatietype;
  reverseRelatieType: ZaakRelatietype;
}
