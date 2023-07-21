/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Cardinaliteit } from "./cardinaliteit";

export class PersonenParameters {
  bsn: Cardinaliteit;
  geslachtsnaam: Cardinaliteit;
  voornamen: Cardinaliteit;
  voorvoegsel: Cardinaliteit;
  geboortedatum: Cardinaliteit;
  gemeenteVanInschrijving: Cardinaliteit;
  postcode: Cardinaliteit;
  huisnummer: Cardinaliteit;
  straat: Cardinaliteit;
}
