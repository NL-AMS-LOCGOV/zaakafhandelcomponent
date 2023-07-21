/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot } from "@angular/router";
import { Observable } from "rxjs";
import { Zaak } from "./model/zaak";
import { ZakenService } from "./zaken.service";

@Injectable({
  providedIn: "root",
})
export class ZaakIdentificatieResolver {
  constructor(private zakenService: ZakenService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Zaak> {
    const zaakID: string = route.paramMap.get("zaakIdentificatie");
    return this.zakenService.readZaakByID(zaakID);
  }
}
