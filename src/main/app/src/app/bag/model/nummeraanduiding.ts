/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { BAGObject } from "./bagobject";
import { Woonplaats } from "./woonplaats";
import { OpenbareRuimte } from "./openbare-ruimte";

export class Nummeraanduiding extends BAGObject {
  postcode: string;
  huisnummerWeergave: string;
  huisnummer: string;
  huisletter: string;
  huisnummertoevoeging: string;
  openbareRuimteNaam: string;
  status: "UITGEGEVEN" | "INGETROKKEN";
  woonplaats: Woonplaats;
  openbareRuimte: OpenbareRuimte;
}
