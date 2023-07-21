/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Inject, LOCALE_ID, Pipe, PipeTransform } from "@angular/core";
import * as moment from "moment";

@Pipe({ name: "datum" })
export class DatumPipe implements PipeTransform {
  constructor(@Inject(LOCALE_ID) public locale: string) {}

  transform(value: Date | moment.Moment | string, dateFormat?: string): any {
    if (value) {
      const m: moment.Moment = moment(value, moment.ISO_8601).locale(
        this.locale,
      );
      if (m.isValid()) {
        return m.format(this.getFormat(dateFormat)).replace(/-/g, "\u2011");
        // Format dates with hard non breaking hyphens, because the normal soft hyphens in a date will be seen
        // by the browser as a point where a new line can be started if necessary. Replacing soft hyphens with
        // hard hyphens prevents that meaning that the date will either remain on the same line or moved as a
        // whole to the next line.
      }
    }
    return value;
  }

  // angular date format mappen op moment formaat
  getFormat(dateFormat: string): string {
    if (!dateFormat || dateFormat === "shortDate") {
      return "L";
    } else if (dateFormat === "mediumDate") {
      return "ll";
    } else if (dateFormat === "longDate") {
      return "LL";
    } else if (dateFormat === "short") {
      return "L LT";
    } else if (dateFormat === "medium") {
      return "ll LT";
    } else if (dateFormat === "long") {
      return "LL LT";
    } else if (dateFormat === "mediumDate") {
      return "LL";
    } else if (dateFormat === "full") {
      return "LLLL";
    } else if (dateFormat === "fullDate") {
      return "dddd, LL";
    }
    return dateFormat;
  }
}
