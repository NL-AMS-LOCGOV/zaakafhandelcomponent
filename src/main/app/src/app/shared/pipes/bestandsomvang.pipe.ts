/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Inject, LOCALE_ID, Pipe, PipeTransform } from "@angular/core";

@Pipe({ name: "bestandsomvang" })
export class BestandsomvangPipe implements PipeTransform {
  constructor(@Inject(LOCALE_ID) public locale: string) {}

  transform(value: number): any {
    if (value) {
      const stringValue =
        value / 1000000 < 1
          ? Math.round(value / 1000) + " kB"
          : (value / 1000000).toFixed(2) + " MB";
      return stringValue;
    }
    return value;
  }
}
