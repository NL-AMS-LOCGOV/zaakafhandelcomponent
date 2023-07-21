/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, RouterStateSnapshot } from "@angular/router";
import { Observable } from "rxjs";
import { ReferentieTabelService } from "./referentie-tabel.service";
import { ReferentieTabel } from "./model/referentie-tabel";

@Injectable({
  providedIn: "root",
})
export class ReferentieTabelResolver {
  constructor(private adminService: ReferentieTabelService) {}

  resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
  ): Observable<ReferentieTabel> {
    return this.adminService.readReferentieTabel(route.paramMap.get("id"));
  }
}
