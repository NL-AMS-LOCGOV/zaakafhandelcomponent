/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Pipe, PipeTransform } from "@angular/core";
import * as moment from "moment";
import { TranslateService } from "@ngx-translate/core";

@Pipe({ name: "dagen" })
export class DagenPipe implements PipeTransform {
  constructor(private translate: TranslateService) {}

  transform(value: any): string {
    if (value) {
      const vandaag = moment().startOf("day");
      const verloopt = moment(value).startOf("day");
      const dagen = verloopt.diff(vandaag, "days");
      if (0 <= dagen) {
        switch (dagen) {
          case 0:
            return this.translate.instant("verloopt.vandaag");
          case 1:
            return this.translate.instant("verloopt.over.dag");
          default:
            return this.translate.instant("verloopt.over.dagen", {
              dagen: dagen,
            });
        }
      }
    }
    return null;
  }
}
