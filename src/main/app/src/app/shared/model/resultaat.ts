/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export class Resultaat<TYPE> {
  resultaten: TYPE[];
  totaal: number;
  foutmelding: string;
}
