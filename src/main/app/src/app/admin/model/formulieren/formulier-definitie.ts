/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { FormulierVeldDefinitie } from "./formulier-veld-definitie";

export class FormulierDefinitie {
  id: number;
  systeemnaam: string;
  naam: string;
  beschrijving: string;
  uitleg: string;
  creatiedatum: string;
  wijzigingsdatum: string;
  veldDefinities: FormulierVeldDefinitie[];
}
