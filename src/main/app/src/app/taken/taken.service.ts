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

@Injectable({
    providedIn: 'root'
})
export class TakenService {

    private basepath: string = '/zac/rest/taken';

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {
    }

    getTaak(id: string): Observable<Taak> {
        return this.http.get<Taak>(`${this.basepath}/${id}`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    getTakenVoorZaak(uuid: string): Observable<Taak[]> {
        return this.http.get<Taak[]>(`${this.basepath}/zaak/${uuid}`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    getMijnTaken(request: TableRequest): Observable<TableResponse<Taak>> {
        return this.http.get<TableResponse<Taak>>(`${this.basepath}/mijn`, {
            params: TakenService.getTableParams(request)
        }).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    getWerkvoorraadTaken(request: TableRequest): Observable<TableResponse<Taak>> {
        return this.http.get<TableResponse<Taak>>(`${this.basepath}/werkvoorraad`, {
            params: TakenService.getTableParams(request)
        }).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    toekennen(taak: Taak): Observable<Medewerker> {
        return this.http.patch<Medewerker>(`${this.basepath}/toekennen`, taak).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    toekennenAanIngelogdeGebruiker(taak: Taak): Observable<Medewerker> {
        let taakToekennenGegevens: TaakToekennenGegevens = new TaakToekennenGegevens();
        taakToekennenGegevens.taakId = taak.id;
        taakToekennenGegevens.zaakUuid = taak.zaakUUID;

        return this.http.patch<Medewerker>(`${this.basepath}/toekennen/mij`, taakToekennenGegevens).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    bewerken(taak: Taak): Observable<Taak> {
        return this.http.patch<Taak>(`${this.basepath}/bewerken`, taak).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    afronden(taak: Taak): Observable<Taak> {
        return this.http.patch<Taak>(`${this.basepath}/afronden`, taak).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    private static getTableParams(request: TableRequest): HttpParams {
        return new HttpParams()
        .set('tableRequest', JSON.stringify(request));
    }
}
