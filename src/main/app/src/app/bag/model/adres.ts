/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { BAGObject } from "./bagobject";
import { Woonplaats } from "./woonplaats";
import { OpenbareRuimte } from "./openbare-ruimte";
import { Nummeraanduiding } from "./nummeraanduiding";
import { Pand } from "./pand";
import { AdresseerbaarObject } from "./adresseerbaar-object";
import { Geometry } from "../../zaken/model/geometry";

export class Adres extends BAGObject {
  postcode: string;
  huisnummerWeergave: string;
  huisnummer: string;
  huisletter: string;
  huisnummertoevoeging: string;
  openbareRuimteNaam: string;
  woonplaatsNaam: string;
  openbareRuimte: OpenbareRuimte;
  nummeraanduiding: Nummeraanduiding;
  woonplaats: Woonplaats;
  adresseerbaarObject: AdresseerbaarObject;
  panden: Pand[];
  geometry: Geometry;
}
