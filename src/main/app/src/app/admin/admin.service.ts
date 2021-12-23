/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpParams} from '@angular/common/http';
import {TableRequest} from '../shared/dynamic-table/datasource/table-request';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {Observable} from 'rxjs';
import {Zaaktype} from '../zaken/model/zaaktype';
import {catchError} from 'rxjs/operators';
import {Zaak} from '../zaken/model/zaak';
import {ZaakafhandelParameters} from './model/zaakafhandel-parameters';
import {CaseModel} from './model/case-model';
import {ZaakOverzicht} from '../zaken/model/zaak-overzicht';
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
        return this.http.get<ZaakafhandelParameters[]>(`${this.basepath}/params`).pipe(
            catchError(this.handleError)
        );
    }

    readZaakafhandelparameters(zaaktypeUuid: string): Observable<ZaakafhandelParameters> {
        return this.http.get<ZaakafhandelParameters>(`${this.basepath}/params/${zaaktypeUuid}`).pipe(
            catchError(this.handleError)
        );
    }

    listCaseModels(): Observable<CaseModel[]> {
        return this.http.get<CaseModel[]>(`${this.basepath}/caseModel`).pipe(
            catchError(this.handleError)
        );
    }

    readCasemodel(key: string): Observable<CaseModel> {
        return this.http.get<CaseModel>(`${this.basepath}/caseModel/${key}`).pipe(
            catchError(this.handleError)
        );
    }

    listFormulierDefinities(): Observable<FormulierDefinitieVerwijzing[]> {
        return this.http.get<FormulierDefinitieVerwijzing[]>(`${this.basepath}/formulierDefinities`).pipe(
            catchError(this.handleError)
        );
    }

    updateZaakafhandelparameters(zaakafhandelparameters): Observable<void> {
        return this.http.put<void>(`${this.basepath}/params`, zaakafhandelparameters).pipe(
            catchError(this.handleError)
        );
    }

    private handleError(err: HttpErrorResponse): Observable<never> {
        return this.foutAfhandelingService.redirect(err);
    }
}
