/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { ZaakbeeindigReden } from "./zaakbeeindig-reden";
import { Resultaattype } from "../../zaken/model/resultaattype";

export class ZaakbeeindigParameter {
  id: string;
  zaakbeeindigReden: ZaakbeeindigReden;
  resultaattype: Resultaattype;
}
