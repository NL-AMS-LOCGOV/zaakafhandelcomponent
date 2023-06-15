/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {Observable} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {FormulierDefinitie} from './model/formulieren/formulier-definitie';

@Injectable({
    providedIn: 'root'
})
export class FormulierDefinitieService {

    private basepath: string = '/rest/formulierDefinities';

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {
    }

    create(definitie: FormulierDefinitie): Observable<FormulierDefinitie> {
        return this.http.post<FormulierDefinitie>(`${this.basepath}`, definitie).pipe(
                catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    read(id: number | string): Observable<FormulierDefinitie> {
        return this.http.get<FormulierDefinitie[]>(`${this.basepath}/${id}`).pipe(
                catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    run(systeemnaam: string): Observable<FormulierDefinitie> {
        return this.http.get<FormulierDefinitie[]>(`${this.basepath}/runtime/${systeemnaam}`).pipe(
                catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    update(definitie: FormulierDefinitie): Observable<FormulierDefinitie> {
        return this.http.put<FormulierDefinitie>(`${this.basepath}`, definitie).pipe(
                catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.basepath}/${id}`).pipe(
                catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    list(): Observable<FormulierDefinitie[]> {
        return this.http.get<FormulierDefinitie[]>(`${this.basepath}`).pipe(
                catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }
}
