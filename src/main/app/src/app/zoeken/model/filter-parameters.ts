/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export class FilterParameters {
  waarden: string[];
  inverse: string;

  constructor(waarden: string[], inverse: boolean) {
    this.waarden = waarden;
    this.inverse = String(inverse);
  }
}
