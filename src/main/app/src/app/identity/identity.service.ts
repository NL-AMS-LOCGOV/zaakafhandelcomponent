/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {Observable, of} from 'rxjs';
import {catchError, tap} from 'rxjs/operators';
import {Groep} from './model/groep';
import {HttpClient} from '@angular/common/http';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {Medewerker} from './model/medewerker';
import {SessionStorageUtil} from '../shared/storage/session-storage.util';

@Injectable({
    providedIn: 'root'
})
export class IdentityService {

    private basepath: string = '/rest/identity';

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {
    }

    listGroepen(): Observable<Groep[]> {
        return this.http.get<Groep[]>(`${this.basepath}/groepen`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listMedewerkersInGroep(groepId: string): Observable<Medewerker[]> {
        return this.http.get<Medewerker[]>(`${this.basepath}/groepen/${groepId}/medewerkers`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listMedewerkers(): Observable<Medewerker[]> {
        return this.http.get<Medewerker[]>(`${this.basepath}/medewerkers`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    readIngelogdeMedewerker(): Observable<Medewerker> {
        const ingelogdeMedewerker = SessionStorageUtil.getSessionStorage('ingelogdeMedewerker');
        if (ingelogdeMedewerker) {
            return of(ingelogdeMedewerker);
        }
        return this.http.get<Medewerker>(`${this.basepath}/ingelogdemedewerker`).pipe(
            tap(medewerker => {
                SessionStorageUtil.setSessionStorage('ingelogdeMedewerker', medewerker);
            }),
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }
}
