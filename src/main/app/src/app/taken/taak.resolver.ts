/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, RouterStateSnapshot } from "@angular/router";
import { Observable } from "rxjs";
import { TakenService } from "./taken.service";
import { Taak } from "./model/taak";

@Injectable({
  providedIn: "root",
})
export class TaakResolver {
  constructor(private takenService: TakenService) {}

  resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
  ): Observable<Taak> {
    const taakID: string = route.paramMap.get("id");
    return this.takenService.readTaak(taakID);
  }
}
