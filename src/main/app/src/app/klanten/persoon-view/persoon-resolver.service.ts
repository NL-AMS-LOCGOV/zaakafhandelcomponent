/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, RouterStateSnapshot } from "@angular/router";
import { Observable } from "rxjs";
import { KlantenService } from "../klanten.service";
import { Persoon } from "../model/personen/persoon";

@Injectable({
  providedIn: "root",
})
export class PersoonResolverService {
  constructor(private klantenService: KlantenService) {}

  resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
  ): Observable<Persoon> {
    const bsn: string = route.paramMap.get("bsn");
    return this.klantenService.readPersoon(bsn);
  }
}
