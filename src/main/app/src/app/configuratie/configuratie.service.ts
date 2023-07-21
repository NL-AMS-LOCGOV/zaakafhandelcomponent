/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { FoutAfhandelingService } from "../fout-afhandeling/fout-afhandeling.service";
import { Observable } from "rxjs";
import { catchError, shareReplay } from "rxjs/operators";
import { Taal } from "./model/taal";

@Injectable({
  providedIn: "root",
})
export class ConfiguratieService {
  private readonly basepath = "/rest/configuratie";
  private talen$: Observable<Taal[]>;
  private defaultTaal$: Observable<Taal>;
  private maxFileSizeMB$: Observable<number>;
  private additionalAllowedFileTypes$: Observable<string[]>;
  private gemeenteCode$: Observable<string>;
  private gemeenteNaam$: Observable<string>;

  constructor(
    private http: HttpClient,
    private foutAfhandelingService: FoutAfhandelingService,
  ) {}

  listTalen(): Observable<Taal[]> {
    if (!this.talen$) {
      this.talen$ = this.http.get<Taal[]>(`${this.basepath}/talen`).pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
        shareReplay(1),
      );
    }
    return this.talen$;
  }

  readDefaultTaal(): Observable<Taal> {
    if (!this.defaultTaal$) {
      this.defaultTaal$ = this.http
        .get<Taal>(`${this.basepath}/talen/default`)
        .pipe(
          catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
          shareReplay(1),
        );
    }
    return this.defaultTaal$;
  }

  readMaxFileSizeMB(): Observable<number> {
    if (!this.maxFileSizeMB$) {
      this.maxFileSizeMB$ = this.http
        .get<number>(`${this.basepath}/maxFileSizeMB`)
        .pipe(
          catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
          shareReplay(1),
        );
    }
    return this.maxFileSizeMB$;
  }

  readAdditionalAllowedFileTypes(): Observable<string[]> {
    if (!this.additionalAllowedFileTypes$) {
      this.additionalAllowedFileTypes$ = this.http
        .get<string[]>(`${this.basepath}/additionalAllowedFileTypes`)
        .pipe(
          catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
          shareReplay(1),
        );
    }
    return this.additionalAllowedFileTypes$;
  }

  readGemeenteCode(): Observable<string> {
    if (!this.gemeenteCode$) {
      this.gemeenteCode$ = this.http
        .get<string>(`${this.basepath}/gemeente/code`)
        .pipe(
          catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
          shareReplay(1),
        );
    }
    return this.gemeenteCode$;
  }

  readGemeenteNaam(): Observable<string> {
    if (!this.gemeenteNaam$) {
      this.gemeenteNaam$ = this.http
        .get<string>(`${this.basepath}/gemeente`)
        .pipe(
          catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
          shareReplay(1),
        );
    }
    return this.gemeenteNaam$;
  }
}
