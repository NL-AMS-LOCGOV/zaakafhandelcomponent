/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { FoutAfhandelingService } from "../fout-afhandeling/fout-afhandeling.service";
import { Observable } from "rxjs";
import { catchError } from "rxjs/operators";
import { SignaleringSettings } from "./model/signalering-settings";

@Injectable({
  providedIn: "root",
})
export class SignaleringenSettingsService {
  private basepath = "/rest/signaleringen";

  constructor(
    private http: HttpClient,
    private foutAfhandelingService: FoutAfhandelingService,
  ) {}

  list(): Observable<SignaleringSettings[]> {
    return this.http
      .get<SignaleringSettings[]>(`${this.basepath}/instellingen`)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  put(instellingen: SignaleringSettings): Observable<void> {
    return this.http
      .put<void>(`${this.basepath}/instellingen`, instellingen)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }
}
