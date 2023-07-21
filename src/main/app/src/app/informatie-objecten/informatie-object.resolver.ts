/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, RouterStateSnapshot } from "@angular/router";
import { Observable } from "rxjs";
import { EnkelvoudigInformatieobject } from "./model/enkelvoudig-informatieobject";
import { InformatieObjectenService } from "./informatie-objecten.service";

@Injectable({
  providedIn: "root",
})
export class InformatieObjectResolver {
  constructor(private informatieObjectenService: InformatieObjectenService) {}

  resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
  ): Observable<EnkelvoudigInformatieobject> {
    const informatieObjectUUID: string = route.paramMap.get("uuid");
    const informatieObjectVersie: string = route.paramMap.get("versie");

    if (informatieObjectVersie) {
      return this.informatieObjectenService.readEnkelvoudigInformatieobjectVersie(
        informatieObjectUUID,
        Number(informatieObjectVersie),
      );
    }
    return this.informatieObjectenService.readEnkelvoudigInformatieobject(
      informatieObjectUUID,
    );
  }
}
