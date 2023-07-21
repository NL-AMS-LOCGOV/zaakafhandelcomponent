/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Pipe, PipeTransform } from "@angular/core";
import { LocationUtil } from "../location/location-util";
import { Geometry } from "../../zaken/model/geometry";

@Pipe({
  name: "location",
})
export class LocationPipe implements PipeTransform {
  constructor() {}

  transform(value: Geometry | string): string {
    if (value) {
      return LocationUtil.format(
        typeof value == "string" ? LocationUtil.wktToPoint(value) : value,
      );
    }
    return null;
  }
}
