/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, RouterStateSnapshot } from "@angular/router";
import { Observable } from "rxjs";
import { ZaakafhandelParametersService } from "./zaakafhandel-parameters.service";
import { ZaakafhandelParameters } from "./model/zaakafhandel-parameters";

@Injectable({
  providedIn: "root",
})
export class ZaakafhandelParametersResolver {
  constructor(private adminService: ZaakafhandelParametersService) {}

  resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
  ): Observable<ZaakafhandelParameters> {
    return this.adminService.readZaakafhandelparameters(
      route.paramMap.get("uuid"),
    );
  }
}
