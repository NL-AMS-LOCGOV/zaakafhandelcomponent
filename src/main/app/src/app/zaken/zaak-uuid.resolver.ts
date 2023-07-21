/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, RouterStateSnapshot } from "@angular/router";
import { Observable } from "rxjs";
import { Zaak } from "./model/zaak";
import { ZakenService } from "./zaken.service";

@Injectable({
  providedIn: "root",
})
export class ZaakUuidResolver {
  constructor(private zakenService: ZakenService) {}

  resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
  ): Observable<Zaak> {
    const zaakUuid: string = route.paramMap.get("zaakUuid");
    return this.zakenService.readZaak(zaakUuid);
  }
}
