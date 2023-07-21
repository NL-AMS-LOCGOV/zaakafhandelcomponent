/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Pipe, PipeTransform } from "@angular/core";

@Pipe({ name: "tooltipList" })
export class TooltipListPipe implements PipeTransform {
  transform(lines: string[]): string {
    let list = "";

    if (lines) {
      lines.forEach((line) => {
        list += "• " + line + "\n";
      });
    }

    return list;
  }
}
