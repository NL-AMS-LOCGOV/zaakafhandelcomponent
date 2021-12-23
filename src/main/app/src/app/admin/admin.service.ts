/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {Observable} from 'rxjs';
import {Zaaktype} from '../zaken/model/zaaktype';
import {catchError} from 'rxjs/operators';
import {ZaakafhandelParameters} from './model/zaakafhandel-parameters';
import {CaseDefinition} from './model/case-definition';
import {FormulierDefinitieVerwijzing} from './model/formulier-definitie-verwijzing';

@Injectable({
    providedIn: 'root'
})
export class AdminService {

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {
    }

    private basepath = '/rest/admin';

    listZaaktypes(): Observable<Zaaktype[]> {
        return this.http.get<Zaaktype[]>(`${this.basepath}/zaaktype`).pipe(
            catchError(this.handleError)
        );
    }

    listZaakafhandelParameters(): Observable<ZaakafhandelParameters[]> {
        return this.http.get<ZaakafhandelParameters[]>(`${this.basepath}/parameters`).pipe(
            catchError(this.handleError)
        );
    }

    readZaakafhandelparameters(zaaktypeUuid: string): Observable<ZaakafhandelParameters> {
        return this.http.get<ZaakafhandelParameters>(`${this.basepath}/parameters/${zaaktypeUuid}`).pipe(
            catchError(this.handleError)
        );
    }

    listCaseDefinitions(): Observable<CaseDefinition[]> {
        return this.http.get<CaseDefinition[]>(`${this.basepath}/caseDefinition`).pipe(
            catchError(this.handleError)
        );
    }

    readCaseDefinition(key: string): Observable<CaseDefinition> {
        return this.http.get<CaseDefinition>(`${this.basepath}/caseDefinition/${key}`).pipe(
            catchError(this.handleError)
        );
    }

    listFormulierDefinities(): Observable<FormulierDefinitieVerwijzing[]> {
        return this.http.get<FormulierDefinitieVerwijzing[]>(`${this.basepath}/formulierDefinities`).pipe(
            catchError(this.handleError)
        );
    }

    updateZaakafhandelparameters(zaakafhandelparameters): Observable<void> {
        return this.http.put<void>(`${this.basepath}/parameters`, zaakafhandelparameters).pipe(
            catchError(this.handleError)
        );
    }

    private handleError(err: HttpErrorResponse): Observable<never> {
        return this.foutAfhandelingService.redirect(err);
    }
}
