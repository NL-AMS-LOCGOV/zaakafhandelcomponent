/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { FoutAfhandelingService } from "../fout-afhandeling/fout-afhandeling.service";
import { Observable } from "rxjs";
import { catchError } from "rxjs/operators";
import { MailtemplateKoppeling } from "./model/mailtemplate-koppeling";

@Injectable({
  providedIn: "root",
})
export class MailtemplateKoppelingService {
  private basepath = "/rest/beheer/mailtemplatekoppeling";

  constructor(
    private http: HttpClient,
    private foutAfhandelingService: FoutAfhandelingService,
  ) {}

  readMailtemplateKoppeling(id: string): Observable<MailtemplateKoppeling> {
    return this.http
      .get<MailtemplateKoppeling[]>(`${this.basepath}/${id}`)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  listMailtemplateKoppelingen(): Observable<MailtemplateKoppeling[]> {
    return this.http
      .get<MailtemplateKoppeling[]>(`${this.basepath}`)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  deleteMailtemplateKoppeling(id: number): Observable<void> {
    return this.http
      .delete<void>(`${this.basepath}/${id}`)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  storeMailtemplateKoppeling(
    mailtemplateKoppeling: MailtemplateKoppeling,
  ): Observable<MailtemplateKoppeling> {
    return this.http
      .put<MailtemplateKoppeling>(`${this.basepath}`, mailtemplateKoppeling)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }
}
