/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { FoutAfhandelingService } from "../fout-afhandeling/fout-afhandeling.service";
import { Observable } from "rxjs";
import { catchError } from "rxjs/operators";
import { ListParameters } from "../shared/model/list-parameters";
import { InboxProductaanvraagResultaat } from "./model/inbox-productaanvraag-resultaat";
import { InboxProductaanvraag } from "./model/inbox-productaanvraag";

@Injectable({
  providedIn: "root",
})
export class InboxProductaanvragenService {
  private basepath = "/rest/inbox-productaanvragen";

  constructor(
    private http: HttpClient,
    private foutAfhandelingService: FoutAfhandelingService,
  ) {}

  list(parameters: ListParameters): Observable<InboxProductaanvraagResultaat> {
    return this.http
      .put<InboxProductaanvraagResultaat>(this.basepath, parameters)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  delete(ip: InboxProductaanvraag): Observable<void> {
    return this.http.delete<void>(`${this.basepath}/${ip.id}`);
  }

  pdfPreview(aanvraagdocumentUUID: string): string {
    return `${this.basepath}/${aanvraagdocumentUUID}/pdfPreview`;
  }
}
