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
import {SessionStorageService} from '../shared/storage/session-storage.service';

@Injectable({
    providedIn: 'root'
})
export class IdentityService {

    private basepath: string = '/rest/identity';

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService, private sessionStorageService: SessionStorageService) {
    }

    getGroepen(): Observable<Groep[]> {
        return this.http.get<Groep[]>(`${this.basepath}/groepen`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    getMedewerkersInGroep(groepId: string): Observable<Medewerker[]> {
        return this.http.get<Medewerker[]>(`${this.basepath}/groepen/${groepId}/medewerkers`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    getIngelogdeMedewerker(): Observable<Medewerker> {
        const ingelogdeMedewerker = this.sessionStorageService.getSessionStorage('ingelogdeMedewerker');
        if (ingelogdeMedewerker) {
            return of(ingelogdeMedewerker);
        }
        return this.http.get<Medewerker>(`${this.basepath}/ingelogdemedewerker`).pipe(
            tap(medewerker => {
                this.sessionStorageService.setSessionStorage('ingelogdeMedewerker', medewerker);
            }),
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }
}
