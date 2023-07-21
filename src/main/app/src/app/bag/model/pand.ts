/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { BAGObject } from "./bagobject";
import { Geometry } from "../../zaken/model/geometry";

export class Pand extends BAGObject {
  oorspronkelijkBouwjaar: string;
  status:
    | "BOUWVERGUNNING_VERLEEND"
    | "NIET_GEREALISEERD_PAND"
    | "BOUW_GESTART"
    | "PAND_IN_GEBRUIK_NIET_INGEMETEN_"
    | "PAND_IN_GEBRUIK"
    | "VERBOUWING_PAND"
    | "SLOOPVERGUNNING_VERLEEND"
    | "PAND_GESLOOPT"
    | "PAND_BUITEN_GEBRUIK"
    | "PAND_TEN_ONRECHTE_OPGEVOERD";
  statusWeergave: string;
  geometry: Geometry;
}
