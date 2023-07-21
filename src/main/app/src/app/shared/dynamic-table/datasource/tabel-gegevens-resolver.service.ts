/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, RouterStateSnapshot } from "@angular/router";
import { Observable } from "rxjs";
import { TabelGegevens } from "../model/tabel-gegevens";
import { GebruikersvoorkeurenService } from "../../../gebruikersvoorkeuren/gebruikersvoorkeuren.service";

@Injectable({
  providedIn: "root",
})
export class TabelGegevensResolver {
  constructor(
    private gebruikersvoorkeurenService: GebruikersvoorkeurenService,
  ) {}

  resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
  ): Observable<TabelGegevens> {
    return this.gebruikersvoorkeurenService.readTabelGegevens(
      route.data.werklijst,
    );
  }
}
