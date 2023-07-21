/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { FoutAfhandelingService } from "../fout-afhandeling/fout-afhandeling.service";
import { Observable } from "rxjs";
import { Notitie } from "./model/notitie";
import { catchError } from "rxjs/operators";

@Injectable({
  providedIn: "root",
})
export class NotitieService {
  private basepath = "/rest/notities";

  constructor(
    private http: HttpClient,
    private foutAfhandelingService: FoutAfhandelingService,
  ) {}

  listNotities(type: string, uuid: string): Observable<Notitie[]> {
    return this.http
      .get<Notitie[]>(`${this.basepath}/${type}/${uuid}`)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  createNotitie(notitie: Notitie): Observable<Notitie> {
    return this.http
      .post<Notitie>(`${this.basepath}`, notitie)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  updateNotitie(notitie: Notitie): Observable<Notitie> {
    return this.http
      .patch<Notitie>(`${this.basepath}`, notitie)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  deleteNotitie(id: number): Observable<any> {
    return this.http
      .delete(`${this.basepath}/${id}`)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }
}
