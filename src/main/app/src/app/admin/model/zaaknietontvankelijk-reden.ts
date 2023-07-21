/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { ZaakbeeindigReden } from "./zaakbeeindig-reden";

export class ZaaknietontvankelijkReden extends ZaakbeeindigReden {
  private static ZNOR = new ZaaknietontvankelijkReden();
  id = "znor";
  naam = "Zaak is niet ontvankelijk";

  private constructor() {
    super();
  }

  static getInstance() {
    return this.ZNOR;
  }

  static is(reden: ZaakbeeindigReden): boolean {
    return reden.id === this.ZNOR.id;
  }
}
