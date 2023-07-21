/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { catchError } from "rxjs/operators";
import { HttpClient } from "@angular/common/http";
import { FoutAfhandelingService } from "../fout-afhandeling/fout-afhandeling.service";
import { WerklijstRechten } from "./model/werklijst-rechten";
import { OverigeRechten } from "./model/overige-rechten";

@Injectable({
  providedIn: "root",
})
export class PolicyService {
  constructor(
    private http: HttpClient,
    private foutAfhandelingService: FoutAfhandelingService,
  ) {}

  private basepath = "/rest/policy";

  readWerklijstRechten(): Observable<WerklijstRechten> {
    return this.http
      .get<WerklijstRechten>(`${this.basepath}/werklijstRechten`)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  readOverigeRechten(): Observable<OverigeRechten> {
    return this.http
      .get<OverigeRechten>(`${this.basepath}/overigeRechten`)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }
}
