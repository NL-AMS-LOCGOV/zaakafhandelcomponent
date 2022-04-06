/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {Observable} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {OntkoppeldDocument} from './model/ontkoppeld-document';
import {ListParameters} from '../shared/model/list-parameters';
import {Results} from '../shared/model/results';

@Injectable({
    providedIn: 'root'
})
export class OntkoppeldeDocumentenService {

    private basepath: string = '/rest/ontkoppeldedocumenten';

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {
    }

    list(parameters: ListParameters): Observable<Results<OntkoppeldDocument>> {
        return this.http.get<Results<OntkoppeldDocument>>(this.basepath, {
            params: parameters as any
        }).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    private handleError(err: HttpErrorResponse): Observable<never> {
        return this.foutAfhandelingService.redirect(err);
    }

    delete(od: OntkoppeldDocument): Observable<void> {
        return this.http.delete<void>(`${this.basepath}/${od.id}`);
    }
}
