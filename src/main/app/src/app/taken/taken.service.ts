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
import {TableResponse} from '../shared/dynamic-table/datasource/table-response';
import {TaakToekennenGegevens} from './model/taak-toekennen-gegevens';
import {Medewerker} from '../identity/model/medewerker';
import {TaakVerdelenGegevens} from './model/taak-verdelen-gegevens';

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
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listTakenVoorZaak(uuid: string): Observable<Taak[]> {
        return this.http.get<Taak[]>(`${this.basepath}/zaak/${uuid}`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listMijnTaken(request: TableRequest): Observable<TableResponse<Taak>> {
        return this.http.get<TableResponse<Taak>>(`${this.basepath}/mijn`, {
            params: TakenService.getTableParams(request)
        }).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listWerkvoorraadTaken(request: TableRequest): Observable<TableResponse<Taak>> {
        return this.http.get<TableResponse<Taak>>(`${this.basepath}/werkvoorraad`, {
            params: TakenService.getTableParams(request)
        }).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    assign(taak: Taak): Observable<Taak> {
        return this.http.patch<Taak>(`${this.basepath}/assign`, taak).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    assignGroup(taak: Taak): Observable<Taak> {
        return this.http.patch<Taak>(`${this.basepath}/assign/group`, taak).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    assignToLoggedOnUser(taak: Taak): Observable<Taak> {
        const taakToekennenGegevens: TaakToekennenGegevens = new TaakToekennenGegevens();
        taakToekennenGegevens.taakId = taak.id;
        taakToekennenGegevens.zaakUuid = taak.zaakUUID;

        return this.http.patch<Taak>(`${this.basepath}/assignTologgedOnUser`, taakToekennenGegevens).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    update(taak: Taak): Observable<Taak> {
        return this.http.patch<Taak>(`${this.basepath}`, taak).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    updateTaakdata(taak: Taak): Observable<Taak> {
        return this.http.put<Taak>(`${this.basepath}/taakdata`, taak).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    complete(taak: Taak): Observable<Taak> {
        return this.http.patch<Taak>(`${this.basepath}/complete`, taak).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    verdelen(taken: Taak[], medewerker: Medewerker): Observable<void> {
        const taakBody: TaakVerdelenGegevens = new TaakVerdelenGegevens();
        taakBody.taakGegevens = taken.map(taak => ({taakId: taak.id, zaakUuid: taak.zaakUUID}));
        taakBody.behandelaarGebruikersnaam = medewerker.gebruikersnaam;
        return this.http.put<void>(`${this.basepath}/verdelen`, taakBody).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    vrijgeven(taken: Taak[]): Observable<void> {
        const taakBody: TaakVerdelenGegevens = new TaakVerdelenGegevens();
        taakBody.taakGegevens = taken.map(taak => ({taakId: taak.id, zaakUuid: taak.zaakUUID}));
        return this.http.put<void>(`${this.basepath}/vrijgeven`, taakBody).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }
}
