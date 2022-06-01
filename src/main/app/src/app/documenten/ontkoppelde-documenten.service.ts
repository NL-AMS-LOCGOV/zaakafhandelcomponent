/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {Observable} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {OntkoppeldDocument} from './model/ontkoppeld-document';
import {ListParameters} from '../shared/model/list-parameters';
import {Resultaat} from '../shared/model/resultaat';

@Injectable({
    providedIn: 'root'
})
export class OntkoppeldeDocumentenService {

    private basepath: string = '/rest/ontkoppeldedocumenten';

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {
    }

    list(parameters: ListParameters): Observable<Resultaat<OntkoppeldDocument>> {
        return this.http.get<Resultaat<OntkoppeldDocument>>(this.basepath, {
            params: parameters as any
        }).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    delete(od: OntkoppeldDocument): Observable<void> {
        return this.http.delete<void>(`${this.basepath}/${od.id}`);
    }
}