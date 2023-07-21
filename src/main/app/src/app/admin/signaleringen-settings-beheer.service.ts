/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { FoutAfhandelingService } from "../fout-afhandeling/fout-afhandeling.service";
import { Observable } from "rxjs";
import { catchError } from "rxjs/operators";
import { SignaleringSettings } from "../signaleringen/model/signalering-settings";

@Injectable({
  providedIn: "root",
})
export class SignaleringenSettingsBeheerService {
  private basepath = "/rest/signaleringen";

  constructor(
    private http: HttpClient,
    private foutAfhandelingService: FoutAfhandelingService,
  ) {}

  list(groupId: string): Observable<SignaleringSettings[]> {
    return this.http
      .get<SignaleringSettings[]>(
        `${this.basepath}/group/${groupId}/instellingen`,
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  put(groupId: string, instellingen: SignaleringSettings): Observable<void> {
    return this.http
      .put<void>(`${this.basepath}/group/${groupId}/instellingen`, instellingen)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }
}
