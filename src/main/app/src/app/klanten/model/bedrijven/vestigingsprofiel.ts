/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Adres } from "./adres";

export class Vestigingsprofiel {
  vestigingsnummer: string;
  kvkNummer: string;
  rsin: string;
  handelsnaam: string;
  type: string;
  totaalWerkzamePersonen: number;
  deeltijdWerkzamePersonen: number;
  voltijdWerkzamePersonen: number;
  commercieleVestiging: boolean;
  adressen: Adres[];
  website: string;
  sbiHoofdActiviteit: string;
  sbiActiviteiten: string[];
}
