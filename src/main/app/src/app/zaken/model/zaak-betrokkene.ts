/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export class ZaakBetrokkene {
  rolid: string;
  roltype: string;
  roltoelichting: string;
  type:
    | "NATUURLIJK_PERSOON"
    | "NIET_NATUURLIJK_PERSOON"
    | "VESTIGING"
    | "ORGANISATORISCHE_EENHEID"
    | "MEDEWERKER";
  identificatie: string;
}
