/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
  name: "empty",
})
export class EmptyPipe implements PipeTransform {
  transform(value: any, ...args: string[]): string {
    if (args.length > 0) {
      return !value || !value[args[0]] ? "-" : value[args[0]];
    }
    return !value || value.length < 1 ? "-" : value;
  }
}
