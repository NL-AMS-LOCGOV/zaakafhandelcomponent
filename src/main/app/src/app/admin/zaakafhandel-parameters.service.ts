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
import {Resultaattype} from '../zaken/model/resultaattype';
import {ReplyTo} from './model/replyto';
import {FormulierDefinitie} from './model/formulieren/formulier-definitie';

@Injectable({
    providedIn: 'root'
})
export class ZaakafhandelParametersService {

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {
    }

    private basepath = '/rest/zaakafhandelParameters';

    listZaakafhandelParameters(): Observable<ZaakafhandelParameters[]> {
        return this.http.get<ZaakafhandelParameters[]>(`${this.basepath}`).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    readZaakafhandelparameters(zaaktypeUuid: string): Observable<ZaakafhandelParameters> {
        return this.http.get<ZaakafhandelParameters>(`${this.basepath}/${zaaktypeUuid}`).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    listZaakbeeindigRedenen(): Observable<ZaakbeeindigReden[]> {
        return this.http.get<ZaakbeeindigReden[]>(`${this.basepath}/zaakbeeindigRedenen`).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    listZaakbeeindigRedenenForZaaktype(zaaktypeUuid: string): Observable<ZaakbeeindigReden[]> {
        return this.http.get<ZaakbeeindigReden[]>(`${this.basepath}/zaakbeeindigRedenen/${zaaktypeUuid}`).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    listResultaattypes(zaaktypeUuid: string): Observable<Resultaattype[]> {
        return this.http.get<Resultaattype[]>(`${this.basepath}/resultaattypes/${zaaktypeUuid}`).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    listCaseDefinitions(): Observable<CaseDefinition[]> {
        return this.http.get<CaseDefinition[]>(`${this.basepath}/caseDefinition`).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    readCaseDefinition(key: string): Observable<CaseDefinition> {
        return this.http.get<CaseDefinition>(`${this.basepath}/caseDefinition/${key}`).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    updateZaakafhandelparameters(zaakafhandelparameters: ZaakafhandelParameters): Observable<ZaakafhandelParameters> {
        return this.http.put<void>(`${this.basepath}`, zaakafhandelparameters).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    listFormulierDefinities(): Observable<FormulierDefinitie[]> {
        return this.http.get<string[]>(`${this.basepath}/formulierDefinities`).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

    listReplyTos(): Observable<ReplyTo[]> {
        return this.http.get<ReplyTo[]>(`${this.basepath}/replyTo`).pipe(
            catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }
}
