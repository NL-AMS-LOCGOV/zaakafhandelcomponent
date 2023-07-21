/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { ZoekParameters } from "../zoeken/model/zoek-parameters";
import { catchError } from "rxjs/operators";
import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { FoutAfhandelingService } from "../fout-afhandeling/fout-afhandeling.service";
import { Observable } from "rxjs";

@Injectable({
  providedIn: "root",
})
export class CsvService {
  constructor(
    private http: HttpClient,
    private foutAfhandelingService: FoutAfhandelingService,
  ) {}

  private basepath = "/rest/csv";

  exportToCSV(zoekParameters: ZoekParameters): Observable<Blob> {
    return this.http
      .post(`${this.basepath}/export`, zoekParameters, { responseType: "blob" })
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }
}
