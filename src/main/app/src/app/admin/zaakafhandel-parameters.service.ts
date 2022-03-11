/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {Observable} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {ZaakafhandelParameters} from './model/zaakafhandel-parameters';
import {CaseDefinition} from './model/case-definition';
import {FormulierDefinitieVerwijzing} from './model/formulier-definitie-verwijzing';
import {ZaakbeeindigReden} from './model/zaakbeeindig-reden';
import {ZaakResultaat} from '../zaken/model/zaak-resultaat';

@Injectable({
    providedIn: 'root'
})
export class ZaakafhandelParametersService {

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {
    }

    private basepath = '/rest/parameters';

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

    listAllZaakbeeindigRedenen(): Observable<ZaakbeeindigReden[]> {
        return this.http.get<ZaakbeeindigReden[]>(`${this.basepath}/zaakbeeindigRedenen`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listZaakbeeindigRedenen(zaaktypeUuid: string): Observable<ZaakbeeindigReden[]> {
        return this.http.get<ZaakbeeindigReden[]>(`${this.basepath}/zaakbeeindigRedenen/${zaaktypeUuid}`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listZaakResultaten(zaaktypeUuid: string): Observable<ZaakResultaat[]> {
        return this.http.get<ZaakResultaat[]>(`${this.basepath}/resultaten/${zaaktypeUuid}`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
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
