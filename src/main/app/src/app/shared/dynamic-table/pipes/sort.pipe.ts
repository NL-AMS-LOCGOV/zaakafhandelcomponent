/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
  name: "sort",
  pure: true,
})
export class SortPipe implements PipeTransform {
  transform(value: any[], property: string): any[] {
    if (value.length <= 1) {
      return value;
    }
    return value.sort((a, b) => a[property].localeCompare(b[property]));
  }
}
