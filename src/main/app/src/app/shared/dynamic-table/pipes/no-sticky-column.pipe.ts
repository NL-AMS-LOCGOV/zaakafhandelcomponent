/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
  name: "noStickyColumn",
  pure: true,
})
export class NoStickyColumnPipe implements PipeTransform {
  transform(columns: string[], noStickyColumns: string[]): string[] {
    return columns.filter((value) => !noStickyColumns.includes(value));
  }
}
