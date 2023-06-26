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
import {FormulierRuntimeContext} from './model/formulieren/formulier-runtime-context';

@Injectable({
    providedIn: 'root'
})
export class FormulierRuntimeService {

    private basepath: string = '/rest/formulierRuntime';

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {
    }

    run(context: FormulierRuntimeContext): Observable<FormulierDefinitie> {
        return this.http.put<FormulierDefinitie>(`${this.basepath}`, context).pipe(
                catchError(err => this.foutAfhandelingService.foutAfhandelen(err))
        );
    }

}
