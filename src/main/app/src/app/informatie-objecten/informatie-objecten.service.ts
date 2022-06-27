/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {Observable} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {EnkelvoudigInformatieobject} from './model/enkelvoudig-informatieobject';
import {ZaakInformatieobject} from './model/zaak-informatieobject';
import {Informatieobjecttype} from './model/informatieobjecttype';
import {HistorieRegel} from '../shared/historie/model/historie-regel';
import {EnkelvoudigInformatieObjectZoekParameters} from './model/enkelvoudig-informatie-object-zoek-parameters';
import {UtilService} from '../core/service/util.service';
import {Router} from '@angular/router';
import {DocumentVerplaatsGegevens} from './model/document-verplaats-gegevens';
import {EnkelvoudigInformatieObjectVersieGegevens} from './model/enkelvoudig-informatie-object-versie-gegevens';
import {DocumentCreatieGegevens} from './model/document-creatie-gegevens';
import {DocumentCreatieResponse} from './model/document-creatie-response';
import {DocumentVerwijderenGegevens} from './model/document-verwijderen-gegevens';

@Injectable({
    providedIn: 'root'
})
export class InformatieObjectenService {

    private basepath = '/rest/informatieobjecten';

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService, private utilService: UtilService, private router: Router) {
    }

    readEnkelvoudigInformatieobject(uuid: string): Observable<EnkelvoudigInformatieobject> {
        return this.http.get<EnkelvoudigInformatieobject>(`${this.basepath}/informatieobject/${uuid}`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    readEnkelvoudigInformatieobjectVersie(uuid: string, versie: number): Observable<EnkelvoudigInformatieobject> {
        return this.http.get<EnkelvoudigInformatieobject>(`${this.basepath}/informatieobject/versie/${uuid}/${versie}`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listInformatieobjecttypes(zaakTypeID): Observable<Informatieobjecttype[]> {
        return this.http.get<Informatieobjecttype[]>(`${this.basepath}/informatieobjecttypes/${zaakTypeID}`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listInformatieobjecttypesForZaak(zaakUUID): Observable<Informatieobjecttype[]> {
        return this.http.get<Informatieobjecttype[]>(`${this.basepath}/informatieobjecttypes/zaak/${zaakUUID}`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    createEnkelvoudigInformatieobject(zaakUuid: string, documentReferentieId: string,
                                      infoObject: EnkelvoudigInformatieobject,
                                      taakObject: boolean): Observable<EnkelvoudigInformatieobject> {
        return this.http.post<EnkelvoudigInformatieobject>(`${this.basepath}/informatieobject/${zaakUuid}/${documentReferentieId}`, infoObject, {
            params: {
                taakObject: taakObject
            }
        }).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    maakDocument(documentCreatieGegevens: DocumentCreatieGegevens): Observable<DocumentCreatieResponse> {
        return this.http.post<DocumentCreatieResponse>(`${this.basepath}/documentcreatie`, documentCreatieGegevens).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    readHuidigeVersieEnkelvoudigInformatieObject(uuid: string): Observable<EnkelvoudigInformatieObjectVersieGegevens> {
        return this.http.get<EnkelvoudigInformatieObjectVersieGegevens>(`${this.basepath}/informatieobject/${uuid}/huidigeversie`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    partialUpdateEnkelvoudigInformatieobject(documentNieuweVersieGegevens: EnkelvoudigInformatieObjectVersieGegevens): Observable<EnkelvoudigInformatieobject> {
        return this.http.post<EnkelvoudigInformatieobject>(`${this.basepath}/informatieobject/partialupdate`, documentNieuweVersieGegevens).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listEnkelvoudigInformatieobjecten(zoekParameters: EnkelvoudigInformatieObjectZoekParameters): Observable<EnkelvoudigInformatieobject[]> {
        return this.http.put<EnkelvoudigInformatieobject[]>(`${this.basepath}/informatieobjectenList`, zoekParameters).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listZaakInformatieobjecten(uuid: string): Observable<ZaakInformatieobject[]> {
        return this.http.get<ZaakInformatieobject[]>(`${this.basepath}/informatieobject/${uuid}/zaken`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listHistorie(uuid: string): Observable<HistorieRegel[]> {
        return this.http.get<HistorieRegel[]>(`${this.basepath}/informatieobject/${uuid}/historie`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    lockInformatieObject(uuid: string) {
        return this.http.post<void>(`${this.basepath}/informatieobject/${uuid}/lock`, null).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    unlockInformatieObject(uuid: string) {
        return this.http.post<void>(`${this.basepath}/informatieobject/${uuid}/unlock`, null).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    getDownloadURL(uuid: string, versie?: number): string {
        if (versie) {
            return `${this.basepath}/informatieobject/${uuid}/${versie}/download`;
        }
        return `${this.basepath}/informatieobject/${uuid}/download`;
    }

    getUploadURL(uuid: string): string {
        return `${this.basepath}/informatieobject/upload/${uuid}`;
    }

    getPreviewDocument(uuid: string, versie?: number) {
        let url = `${this.basepath}/informatieobject/${uuid}/download`;
        if (versie) {
            url = `${this.basepath}/informatieobject/${uuid}/${versie}/download`;
        }

        return this.http.get(url, {responseType: 'blob'}).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    editEnkelvoudigInformatieObjectInhoud(uuid: string): Observable<string> {
        return this.http.get<string>(`${this.basepath}/informatieobject/${uuid}/edit`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    postVerplaatsDocument(documentVerplaatsGegevens: DocumentVerplaatsGegevens, nieuweZaakID: string): Observable<void> {
        documentVerplaatsGegevens.nieuweZaakID = nieuweZaakID;
        return this.http.post<void>(`${this.basepath}/informatieobject/verplaats`, documentVerplaatsGegevens).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    deleteEnkelvoudigInformatieObject(uuid: string, zaakUuid: string, reden: string): Observable<void> {
        return this.http.delete<void>(`${this.basepath}/informatieobject/${uuid}`, {body: new DocumentVerwijderenGegevens(zaakUuid, reden)}).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }
}
