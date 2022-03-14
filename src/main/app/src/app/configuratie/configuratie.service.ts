/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {Observable} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {Taal} from './model/taal';

@Injectable({
    providedIn: 'root'
})
export class ConfiguratieService {

    private basepath = '/rest/configuratie';

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {
    }

    listTalen(): Observable<Taal[]> {
        return this.http.get<Taal[]>(`${this.basepath}/talen`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    defaultTaal(): Observable<Taal> {
        return this.http.get<Taal>(`${this.basepath}/talen/default`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }
}
