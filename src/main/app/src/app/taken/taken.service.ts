/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {Observable} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {Taak} from './model/taak';
import {TableRequest} from '../shared/dynamic-table/datasource/table-request';
import {TaakToekennenGegevens} from './model/taak-toekennen-gegevens';
import {User} from '../identity/model/user';
import {TaakVerdelenGegevens} from './model/taak-verdelen-gegevens';
import {TaakHistorieRegel} from '../shared/historie/model/taak-historie-regel';
import {TaakZoekObject} from '../zoeken/model/taken/taak-zoek-object';

@Injectable({
    providedIn: 'root'
})
export class TakenService {

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {
    }

    private basepath = '/rest/taken';

    private static getTableParams(request: TableRequest): HttpParams {
        return new HttpParams()
        .set('tableRequest', JSON.stringify(request));
    }

    readTaak(id: string): Observable<Taak> {
        return this.http.get<Taak>(`${this.basepath}/${id}`).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    listTakenVoorZaak(uuid: string): Observable<Taak[]> {
        return this.http.get<Taak[]>(`${this.basepath}/zaak/${uuid}`).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    listHistorieVoorTaak(id: string): Observable<TaakHistorieRegel[]> {
        return this.http.get<TaakHistorieRegel[]>(`${this.basepath}/${id}/historie`).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    assign(taak: Taak): Observable<void> {
        return this.http.patch<void>(`${this.basepath}/assign`, taak).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    assignGroup(taak: Taak): Observable<void> {
        return this.http.patch<void>(`${this.basepath}/assign/group`, taak).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    assignToLoggedOnUser(taak: Taak | TaakZoekObject): Observable<Taak> {
        const taakToekennenGegevens: TaakToekennenGegevens = new TaakToekennenGegevens();
        taakToekennenGegevens.taakId = taak.id;
        taakToekennenGegevens.zaakUuid = taak.zaakUuid;
        return this.http.patch<Taak>(`${this.basepath}/assignTologgedOnUser`, taakToekennenGegevens).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    update(taak: Taak): Observable<Taak> {
        return this.http.patch<Taak>(`${this.basepath}`, taak).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    updateTaakdata(taak: Taak): Observable<Taak> {
        return this.http.put<Taak>(`${this.basepath}/taakdata`, taak).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    complete(taak: Taak): Observable<Taak> {
        return this.http.patch<Taak>(`${this.basepath}/complete`, taak).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    verdelen(taken: TaakZoekObject[], medewerker: User): Observable<void> {
        const taakBody: TaakVerdelenGegevens = new TaakVerdelenGegevens();
        taakBody.taakGegevens = taken.map(taak => ({taakId: taak.id, zaakUuid: taak.zaakUuid}));
        taakBody.behandelaarGebruikersnaam = medewerker.id;
        return this.http.put<void>(`${this.basepath}/verdelen`, taakBody).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    vrijgeven(taken: TaakZoekObject[]): Observable<void> {
        const taakBody: TaakVerdelenGegevens = new TaakVerdelenGegevens();
        taakBody.taakGegevens = taken.map(taak => ({taakId: taak.id, zaakUuid: taak.zaakUuid}));
        return this.http.put<void>(`${this.basepath}/vrijgeven`, taakBody).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    getUploadURL(uuid: string, field: string): string {
        return `${this.basepath}/upload/${uuid}/${field}`;
    }
}
