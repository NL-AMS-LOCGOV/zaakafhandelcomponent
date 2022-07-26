/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {AppActies} from './model/app-acties';
import {Observable} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {HttpClient} from '@angular/common/http';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {ZakenActies} from './model/zaken-acties';
import {TakenActies} from './model/taken-acties';

@Injectable({
    providedIn: 'root'
})
export class PolicyService {

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {}

    private basepath = '/rest/policy';

    readAppActies(): Observable<AppActies> {
        return this.http.get<AppActies>(`${this.basepath}/appActies`).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    readZakenActies(): Observable<ZakenActies> {
        return this.http.get<ZakenActies>(`${this.basepath}/zakenActies`).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    readTakenActies(): Observable<TakenActies> {
        return this.http.get<TakenActies>(`${this.basepath}/takenActies`).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }
}
