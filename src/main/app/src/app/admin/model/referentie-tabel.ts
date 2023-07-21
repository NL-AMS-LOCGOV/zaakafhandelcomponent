/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { ReferentieTabelWaarde } from "./referentie-tabel-waarde";

export class ReferentieTabel {
  id: number;
  code: string;
  naam: string;
  systeem: boolean;
  waarden: ReferentieTabelWaarde[];
}
