/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {HttpClient} from '@angular/common/http';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {WerklijstActies} from './model/werklijst-acties';
import {OverigActies} from './model/overig-acties';

@Injectable({
    providedIn: 'root'
})
export class PolicyService {

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {}

    private basepath = '/rest/policy';

    readWerklijstActies(): Observable<WerklijstActies> {
        return this.http.get<WerklijstActies>(`${this.basepath}/werklijstActies`).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    readOverigActies(): Observable<OverigActies> {
        return this.http.get<OverigActies>(`${this.basepath}/overigActies`).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }
}
