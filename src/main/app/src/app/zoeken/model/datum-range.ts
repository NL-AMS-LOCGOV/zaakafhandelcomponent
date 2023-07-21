/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export class DatumRange {
  van: Date;
  tot: Date;

  constructor(van?: Date, tot?: Date) {
    this.van = van || null;
    this.tot = tot || null;
  }

  hasValue() {
    return this.van != null || this.tot != null;
  }
}
