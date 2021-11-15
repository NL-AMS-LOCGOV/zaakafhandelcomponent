/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {FoutAfhandelingService} from '../../fout-afhandeling/fout-afhandeling.service';
import {Observable} from 'rxjs';
import {Notitie} from './model/notitie';
import {catchError} from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class NotitieService {

    private basepath: string = '/rest/notities';

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {
    }

    getNotities(type: string, uuid: string): Observable<Notitie[]> {

        return this.http.get<Notitie[]>(`${this.basepath}/${type}/${uuid}`).pipe(
            catchError(this.handleError)
        );
    }

    createNotitie(notitie: Notitie): Observable<Notitie> {
        return this.http.post<Notitie>(`${this.basepath}`, notitie).pipe(
            catchError(this.handleError)
        );
    }

    updateNotitie(notitie: Notitie): Observable<Notitie> {
        return this.http.patch<Notitie>(`${this.basepath}`, notitie).pipe(
            catchError(this.handleError)
        );
    }

    deleteNotitie(id: number): Observable<any> {
        return this.http.delete(`${this.basepath}/${id}`).pipe(
            catchError(this.handleError)
        );
    }

    private handleError(err: HttpErrorResponse): Observable<never> {
        return this.foutAfhandelingService.redirect(err);
    }

}
