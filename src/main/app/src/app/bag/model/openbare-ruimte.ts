/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { BAGObject } from "./bagobject";
import { Woonplaats } from "./woonplaats";

export class OpenbareRuimte extends BAGObject {
  naam: string;
  woonplaatsNaam: string;
  type:
    | "WEG"
    | "WATER"
    | "SPOORBAAN"
    | "TERREIN"
    | "KUNSTWERK"
    | "LANDSCHAPPELIJK_GEBIED"
    | "ADMINISTRATIEF_GEBIED";
  typeWeergave: string;
  status: "UITGEGEVEN" | "INGETROKKEN";
  woonplaats: Woonplaats;
}
