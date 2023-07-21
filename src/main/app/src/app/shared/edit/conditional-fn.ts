/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { FormControl } from "@angular/forms";
import * as moment from "moment";

export declare type ConditionalFn = (control: FormControl) => boolean;

export class Conditionals {
  static isAfterDate(actual?: Date | moment.Moment | string): ConditionalFn {
    return (control: FormControl): boolean => {
      return this.isOverschreden(control.value, actual);
    };
  }

  static isOverschreden(
    value: Date | moment.Moment | string,
    actual?: Date | moment.Moment | string,
  ): boolean {
    if (value) {
      const limit: moment.Moment = moment(value, moment.ISO_8601);
      if (actual) {
        return limit.isBefore(moment(actual, moment.ISO_8601));
      } else {
        return limit.isBefore();
      }
    }
    return false;
  }

  static always(): boolean {
    return true;
  }
}
