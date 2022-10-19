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
import {DashboardCardInstelling} from '../dashboard/model/dashboard-card-instelling';

@Injectable({
    providedIn: 'root'
})
export class GebruikersvoorkeurenService {

    private basepath: string = '/rest/gebruikersvoorkeuren';

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {
    }

    listZoekOpdrachten(werklijst: Werklijst): Observable<Zoekopdracht[]> {
        return this.http.get<Zoekopdracht[]>(`${this.basepath}/zoekopdracht/${werklijst}`).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    createOrUpdateZoekOpdrachten(zoekopdracht: Zoekopdracht): Observable<Zoekopdracht> {
        return this.http.post<Zoekopdracht>(`${this.basepath}/zoekopdracht`, zoekopdracht).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    deleteZoekOpdrachten(id: number): Observable<void> {
        return this.http.delete<void>(`${this.basepath}/zoekopdracht/${id}`).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    setZoekopdrachtActief(zoekopdracht: Zoekopdracht): Observable<void> {
        return this.http.put<void>(`${this.basepath}/zoekopdracht/actief`, zoekopdracht).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    removeZoekopdrachtActief(werklijst: Werklijst): Observable<void> {
        return this.http.delete<void>(`${this.basepath}/zoekopdracht/${werklijst}/actief`).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    listDashboardCards(): Observable<DashboardCardInstelling[]> {
        return this.http.get<DashboardCardInstelling[]>(`${this.basepath}/dasboardcard`).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    updateDashboardCards(cards: DashboardCardInstelling[]): Observable<void> {
        return this.http.put<void>(`${this.basepath}/dasboardcard`, cards).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }
}
