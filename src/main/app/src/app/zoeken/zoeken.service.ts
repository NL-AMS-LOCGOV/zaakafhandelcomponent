/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {Observable} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {Resultaat} from '../shared/model/resultaat';
import {ZoekObject} from './model/zoek-object';
import {ZoekParameters} from './model/zoek-parameters';

@Injectable({
    providedIn: 'root'
})
export class ZoekenService {

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {
    }

    private basepath = '/rest/zoeken';


    list(zoekParameters: ZoekParameters): Observable<Resultaat<ZoekObject>> {
        return this.http.put<Resultaat<ZoekObject>>(`${this.basepath}/list`, zoekParameters).pipe(
            catchError(this.handleError)
        );
    }

    private handleError(err: HttpErrorResponse): Observable<never> {
        return this.foutAfhandelingService.redirect(err);
    }
}
