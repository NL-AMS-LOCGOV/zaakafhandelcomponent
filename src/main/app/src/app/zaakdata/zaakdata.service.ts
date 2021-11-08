/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {Observable} from 'rxjs';
import {Zaak} from '../zaken/model/zaak';
import {catchError} from 'rxjs/operators';
import {Zaakdata} from './model/zaakdata';

@Injectable({
    providedIn: 'root'
})
export class ZaakdataService {

    private basepath = '/zac/rest/zaakdata';

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {}

    getZaakdata(zaakUUID: string): Observable<Zaakdata> {
        return this.http.get<Zaakdata>(`${this.basepath}/zaak/${zaakUUID}`).pipe(
            catchError(this.handleError)
        );
    }

    putZaakdata(zaakUUID: string, zaakdata: Zaakdata): Observable<Zaak> {
        return this.http.put<Zaak>(`${this.basepath}/zaak/${zaakUUID}`, zaakdata).pipe(
            catchError(this.handleError)
        );
    }

    private handleError(err: HttpErrorResponse): Observable<never> {
        return this.foutAfhandelingService.redirect(err);
    }
}
