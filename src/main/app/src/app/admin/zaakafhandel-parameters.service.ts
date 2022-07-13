/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {Observable} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {ZaakafhandelParameters} from './model/zaakafhandel-parameters';
import {CaseDefinition} from './model/case-definition';
import {ZaakbeeindigReden} from './model/zaakbeeindig-reden';
import {ZaakResultaat} from '../zaken/model/zaak-resultaat';

@Injectable({
    providedIn: 'root'
})
export class ZaakafhandelParametersService {

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {
    }

    private basepath = '/rest/zaakafhandelParameters';

    listZaakafhandelParameters(): Observable<ZaakafhandelParameters[]> {
        return this.http.get<ZaakafhandelParameters[]>(`${this.basepath}`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    readZaakafhandelparameters(zaaktypeUuid: string): Observable<ZaakafhandelParameters> {
        return this.http.get<ZaakafhandelParameters>(`${this.basepath}/${zaaktypeUuid}`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listZaakbeeindigRedenen(): Observable<ZaakbeeindigReden[]> {
        return this.http.get<ZaakbeeindigReden[]>(`${this.basepath}/zaakbeeindigRedenen`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listZaakbeeindigRedenenForZaaktype(zaaktypeUuid: string): Observable<ZaakbeeindigReden[]> {
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
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    readCaseDefinition(key: string): Observable<CaseDefinition> {
        return this.http.get<CaseDefinition>(`${this.basepath}/caseDefinition/${key}`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    updateZaakafhandelparameters(zaakafhandelparameters: ZaakafhandelParameters): Observable<void> {
        return this.http.put<void>(`${this.basepath}`, zaakafhandelparameters).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }
}
