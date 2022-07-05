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

@Injectable({
    providedIn: 'root'
})
export class PolicyService {

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {}

    private basepath = '/rest/policy';

    readAppActies(): Observable<AppActies> {
        return this.http.get<AppActies>(`${this.basepath}/appActies`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }
}
