/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {Observable} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {ZaaktypeInrichtingscheck} from './model/zaaktype-inrichtingscheck';

@Injectable({
    providedIn: 'root'
})
export class HealthCheckService {

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {
    }

    private basepath = '/rest/health-check';

    listZaaktypeInrichtingschecks(): Observable<ZaaktypeInrichtingscheck[]> {
        return this.http.get<ZaaktypeInrichtingscheck[]>(`${this.basepath}/zaaktypes`).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    getBestaatCommunicatiekanaalEformulier(): Observable<boolean> {
        return this.http.get<ZaaktypeInrichtingscheck[]>(`${this.basepath}/bestaat-communicatiekanaal-eformulier`).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }
}
