/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { FoutAfhandelingService } from "../fout-afhandeling/fout-afhandeling.service";
import { Observable } from "rxjs";
import { Resultaat } from "../shared/model/resultaat";
import { catchError } from "rxjs/operators";
import { ListContactmomentenParameters } from "./model/list-contactmomenten-parameters";
import { Contactmoment } from "./model/contactmoment";

@Injectable({
  providedIn: "root",
})
export class ContactmomentenService {
  constructor(
    private http: HttpClient,
    private foutAfhandelingService: FoutAfhandelingService,
  ) {}

  private basepath = "/rest/contactmomenten";

  listContactmomenten(
    listContactmomentenParameters: ListContactmomentenParameters,
  ): Observable<Resultaat<Contactmoment>> {
    return this.http
      .put<Resultaat<Contactmoment>>(
        `${this.basepath}`,
        listContactmomentenParameters,
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }
}
