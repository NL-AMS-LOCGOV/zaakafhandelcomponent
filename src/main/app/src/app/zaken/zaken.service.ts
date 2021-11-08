/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {Zaak} from './model/zaak';
import {Observable} from 'rxjs';
import {HttpClient, HttpErrorResponse, HttpParams} from '@angular/common/http';
import {catchError} from 'rxjs/operators';
import {TableRequest} from '../shared/dynamic-table/datasource/table-request';
import {TableResponse} from '../shared/dynamic-table/datasource/table-response';
import {Zaaktype} from './model/zaaktype';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {ZaakOverzicht} from './model/zaak-overzicht';
import {ZaakToekennenGegevens} from './model/zaak-toekennen-gegevens';

@Injectable({
    providedIn: 'root'
})
export class ZakenService {

    private basepath = '/zac/rest/zaken';

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {
    }

    getZaak(uuid: string): Observable<Zaak> {
        return this.http.get<Zaak>(`${this.basepath}/zaak/${uuid}`).pipe(
            catchError(this.handleError)
        );
    }

    postZaak(zaak: Zaak): Observable<Zaak> {
        return this.http.post<Zaak>(`${this.basepath}/zaak`, zaak).pipe(
            catchError(this.handleError)
        );
    }

    updateZaak(uuid: string, zaak: Zaak): Observable<Zaak> {
        return this.http.patch<Zaak>(`${this.basepath}/zaak/${uuid}`, zaak).pipe(
            catchError(this.handleError)
        );
    }

    getWerkvoorraadZaken(request: TableRequest): Observable<TableResponse<ZaakOverzicht>> {
        return this.http.get<TableResponse<ZaakOverzicht>>(`${this.basepath}/werkvoorraad`, {
            params: ZakenService.getTableParams(request)
        }).pipe(
            catchError(this.handleError)
        );
    }

    getMijnZaken(request: TableRequest): Observable<TableResponse<ZaakOverzicht>> {
        return this.http.get<TableResponse<ZaakOverzicht>>(`${this.basepath}/mijn`, {
            params: ZakenService.getTableParams(request)
        }).pipe(
            catchError(this.handleError)
        );
    }

    getZaaktypes(): Observable<Zaaktype[]> {
        return this.http.get<Zaaktype[]>(`${this.basepath}/zaaktypes`).pipe(
            catchError(this.handleError)
        );
    }

    toekennen(zaak: Zaak): Observable<Zaak> {
        const zaakBody: ZaakToekennenGegevens = new ZaakToekennenGegevens();
        zaakBody.uuid = zaak.uuid;
        zaakBody.behandelaarGebruikersnaam = zaak.behandelaar?.gebruikersnaam;

        return this.http.put<Zaak>(`${this.basepath}/toekennen`, zaakBody).pipe(
            catchError(this.handleError)
        );
    }

    toekennenAanIngelogdeMedewerker(zaak: Zaak): Observable<Zaak> {
        const zaakBody: ZaakToekennenGegevens = new ZaakToekennenGegevens();
        zaakBody.uuid = zaak.uuid;

        return this.http.put<Zaak>(`${this.basepath}/toekennen/mij`, zaakBody).pipe(
            catchError(this.handleError)
        );
    }

    toekennenAanIngelogdeMedewerkerVanuitLijst(zaak: ZaakOverzicht): Observable<ZaakOverzicht> {
        const zaakBody: ZaakToekennenGegevens = new ZaakToekennenGegevens();
        zaakBody.uuid = zaak.uuid;

        return this.http.put<ZaakOverzicht>(`${this.basepath}/toekennen/mij/lijst`, zaakBody).pipe(
            catchError(this.handleError)
        );
    }

    private static getTableParams(request: TableRequest): HttpParams {
        return new HttpParams().set('tableRequest', JSON.stringify(request));
    }

    private handleError(err: HttpErrorResponse): Observable<never> {
        return this.foutAfhandelingService.redirect(err);
    }
}
