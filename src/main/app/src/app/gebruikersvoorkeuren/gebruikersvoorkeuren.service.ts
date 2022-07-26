/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {catchError} from 'rxjs/operators';

import {HttpClient} from '@angular/common/http';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {Werklijst} from './model/werklijst';
import {Zoekopdracht} from './model/zoekopdracht';

@Injectable({
    providedIn: 'root'
})
export class GebruikersvoorkeurenService {

    private basepath: string = '/rest/gebruikersvoorkeuren';

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {
    }

    listZoekOpdrachten(werklijst: Werklijst): Observable<Zoekopdracht[]> {
        return this.http.get<Zoekopdracht[]>(`${this.basepath}/zoekopdracht/${werklijst}`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    createOrUpdateZoekOpdrachten(zoekopdracht: Zoekopdracht): Observable<Zoekopdracht> {
        return this.http.post<Zoekopdracht>(`${this.basepath}/zoekopdracht`, zoekopdracht).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    deleteZoekOpdrachten(id: number): Observable<void> {
        return this.http.delete<void>(`${this.basepath}/zoekopdracht/${id}`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    setZoekopdrachtActief(zoekopdracht: Zoekopdracht): Observable<void> {
        return this.http.put<void>(`${this.basepath}/zoekopdracht/actief`, zoekopdracht).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    removeZoekopdrachtActief(werklijst: Werklijst): Observable<void> {
        return this.http.delete<void>(`${this.basepath}/zoekopdracht/${werklijst}/actief`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }
}
