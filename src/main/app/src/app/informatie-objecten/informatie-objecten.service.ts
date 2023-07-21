/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { FoutAfhandelingService } from "../fout-afhandeling/fout-afhandeling.service";
import { Observable } from "rxjs";
import { catchError } from "rxjs/operators";
import { EnkelvoudigInformatieobject } from "./model/enkelvoudig-informatieobject";
import { ZaakInformatieobject } from "./model/zaak-informatieobject";
import { Informatieobjecttype } from "./model/informatieobjecttype";
import { HistorieRegel } from "../shared/historie/model/historie-regel";
import { InformatieobjectZoekParameters } from "./model/informatieobject-zoek-parameters";
import { DocumentVerplaatsGegevens } from "./model/document-verplaats-gegevens";
import { EnkelvoudigInformatieObjectVersieGegevens } from "./model/enkelvoudig-informatie-object-versie-gegevens";
import { DocumentCreatieGegevens } from "./model/document-creatie-gegevens";
import { DocumentCreatieResponse } from "./model/document-creatie-response";
import { DocumentVerwijderenGegevens } from "./model/document-verwijderen-gegevens";
import { DocumentVerzendGegevens } from "./model/document-verzend-gegevens";

@Injectable({
  providedIn: "root",
})
export class InformatieObjectenService {
  private basepath = "/rest/informatieobjecten";

  constructor(
    private http: HttpClient,
    private foutAfhandelingService: FoutAfhandelingService,
  ) {}

  // Het EnkelvoudigInformatieobject kan opgehaald worden binnen de context van een specifieke zaak.
  readEnkelvoudigInformatieobject(
    uuid: string,
    zaakUuid?: string,
  ): Observable<EnkelvoudigInformatieobject> {
    return this.http
      .get<EnkelvoudigInformatieobject>(
        InformatieObjectenService.addZaakParameter(
          `${this.basepath}/informatieobject/${uuid}`,
          zaakUuid,
        ),
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  readEnkelvoudigInformatieobjectVersie(
    uuid: string,
    versie: number,
  ): Observable<EnkelvoudigInformatieobject> {
    return this.http
      .get<EnkelvoudigInformatieobject>(
        `${this.basepath}/informatieobject/versie/${uuid}/${versie}`,
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  listInformatieobjecttypes(zaakTypeID): Observable<Informatieobjecttype[]> {
    return this.http
      .get<Informatieobjecttype[]>(
        `${this.basepath}/informatieobjecttypes/${zaakTypeID}`,
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  listInformatieobjecttypesForZaak(
    zaakUUID,
  ): Observable<Informatieobjecttype[]> {
    return this.http
      .get<Informatieobjecttype[]>(
        `${this.basepath}/informatieobjecttypes/zaak/${zaakUUID}`,
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  createEnkelvoudigInformatieobject(
    zaakUuid: string,
    documentReferentieId: string,
    infoObject: EnkelvoudigInformatieobject,
    taakObject: boolean,
  ): Observable<EnkelvoudigInformatieobject> {
    return this.http
      .post<EnkelvoudigInformatieobject>(
        `${this.basepath}/informatieobject/${zaakUuid}/${documentReferentieId}`,
        infoObject,
        {
          params: {
            taakObject: taakObject,
          },
        },
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  createDocument(
    documentCreatieGegevens: DocumentCreatieGegevens,
  ): Observable<DocumentCreatieResponse> {
    return this.http
      .post<DocumentCreatieResponse>(
        `${this.basepath}/documentcreatie`,
        documentCreatieGegevens,
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  readHuidigeVersieEnkelvoudigInformatieObject(
    uuid: string,
  ): Observable<EnkelvoudigInformatieObjectVersieGegevens> {
    return this.http
      .get<EnkelvoudigInformatieObjectVersieGegevens>(
        `${this.basepath}/informatieobject/${uuid}/huidigeversie`,
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  updateEnkelvoudigInformatieobject(
    documentNieuweVersieGegevens: EnkelvoudigInformatieObjectVersieGegevens,
  ): Observable<EnkelvoudigInformatieobject> {
    return this.http
      .post<EnkelvoudigInformatieobject>(
        `${this.basepath}/informatieobject/update`,
        documentNieuweVersieGegevens,
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  listEnkelvoudigInformatieobjecten(
    zoekParameters: InformatieobjectZoekParameters,
  ): Observable<EnkelvoudigInformatieobject[]> {
    return this.http
      .put<EnkelvoudigInformatieobject[]>(
        `${this.basepath}/informatieobjectenList`,
        zoekParameters,
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  readEnkelvoudigInformatieobjectByZaakInformatieobjectUUID(
    uuid: string,
  ): Observable<EnkelvoudigInformatieobject> {
    return this.http
      .get<ZaakInformatieobject>(
        `${this.basepath}/zaakinformatieobject/${uuid}/informatieobject`,
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  listZaakInformatieobjecten(uuid: string): Observable<ZaakInformatieobject[]> {
    return this.http
      .get<ZaakInformatieobject[]>(
        `${this.basepath}/informatieobject/${uuid}/zaakinformatieobjecten`,
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  listInformatieobjectenVoorVerzenden(
    zaakUuid: string,
  ): Observable<EnkelvoudigInformatieobject[]> {
    return this.http
      .get<EnkelvoudigInformatieobject[]>(
        `${this.basepath}/informatieobjecten/zaak/${zaakUuid}/teVerzenden`,
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  verzenden(gegevens: DocumentVerzendGegevens): Observable<void> {
    return this.http
      .post<DocumentCreatieResponse>(
        `${this.basepath}/informatieobjecten/verzenden`,
        gegevens,
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  listHistorie(uuid: string): Observable<HistorieRegel[]> {
    return this.http
      .get<HistorieRegel[]>(
        `${this.basepath}/informatieobject/${uuid}/historie`,
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  lockInformatieObject(uuid: string, zaakUuid: string) {
    return this.http
      .post<void>(
        InformatieObjectenService.addZaakParameter(
          `${this.basepath}/informatieobject/${uuid}/lock`,
          zaakUuid,
        ),
        null,
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  unlockInformatieObject(uuid: string, zaakUuid: string) {
    return this.http
      .post<void>(
        InformatieObjectenService.addZaakParameter(
          `${this.basepath}/informatieobject/${uuid}/unlock`,
          zaakUuid,
        ),
        null,
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  ondertekenInformatieObject(uuid: string, zaakUuid: string) {
    return this.http
      .post<void>(
        InformatieObjectenService.addZaakParameter(
          `${this.basepath}/informatieobject/${uuid}/onderteken`,
          zaakUuid,
        ),
        null,
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  getDownloadURL(uuid: string, versie?: number): string {
    if (versie) {
      return `${this.basepath}/informatieobject/${uuid}/${versie}/download`;
    }
    return `${this.basepath}/informatieobject/${uuid}/download`;
  }

  getZIPDownload(uuids: string[]): Observable<Blob> {
    return this.http
      .post(`${this.basepath}/download/zip`, uuids, { responseType: "blob" })
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  getUploadURL(documentReferentieId: string): string {
    return `${this.basepath}/informatieobject/upload/${documentReferentieId}`;
  }

  getPreviewUrl(uuid: string, versie?: number): string {
    let url = `${this.basepath}/informatieobject/${uuid}/preview`;
    if (versie) {
      url = `${this.basepath}/informatieobject/${uuid}/${versie}/preview`;
    }
    return url;
  }

  editEnkelvoudigInformatieObjectInhoud(
    uuid: string,
    zaakUuid: string,
  ): Observable<string> {
    return this.http
      .get<string>(
        InformatieObjectenService.addZaakParameter(
          `${this.basepath}/informatieobject/${uuid}/edit`,
          zaakUuid,
        ),
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  postVerplaatsDocument(
    documentVerplaatsGegevens: DocumentVerplaatsGegevens,
    nieuweZaakID: string,
  ): Observable<void> {
    documentVerplaatsGegevens.nieuweZaakID = nieuweZaakID;
    return this.http
      .post<void>(
        `${this.basepath}/informatieobject/verplaats`,
        documentVerplaatsGegevens,
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  deleteEnkelvoudigInformatieObject(
    uuid: string,
    zaakUuid: string,
    reden: string,
  ): Observable<void> {
    return this.http
      .delete<void>(`${this.basepath}/informatieobject/${uuid}`, {
        body: new DocumentVerwijderenGegevens(zaakUuid, reden),
      })
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  listZaakIdentificatiesForInformatieobject(
    documentUUID: string,
  ): Observable<string[]> {
    return this.http
      .get<string[]>(
        `${this.basepath}/informatieobject/${documentUUID}/zaakidentificaties`,
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  convertInformatieObjectToPDF(uuid: string, zaakUuid: string) {
    return this.http
      .post<void>(
        InformatieObjectenService.addZaakParameter(
          `${this.basepath}/informatieobject/${uuid}/convert`,
          zaakUuid,
        ),
        null,
      )
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  private static addZaakParameter(url: string, zaakUuid: string): string {
    if (zaakUuid) {
      return url.concat(`?zaak=${zaakUuid}`);
    } else {
      return url;
    }
  }
}
