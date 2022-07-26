/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {Observable} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {ZoekObject} from './model/zoek-object';
import {ZoekParameters} from './model/zoek-parameters';
import {ZoekResultaat} from './model/zoek-resultaat';

@Injectable({
    providedIn: 'root'
})
export class ZoekenService {

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {
    }

    private basepath = '/rest/zoeken';

    list(zoekParameters: ZoekParameters): Observable<ZoekResultaat<ZoekObject>> {
        return this.http.put<ZoekResultaat<ZoekObject>>(`${this.basepath}/list`, zoekParameters).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }
}
