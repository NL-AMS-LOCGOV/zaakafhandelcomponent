/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {PersoonOverzicht} from './model/personen/persoon-overzicht';
import {Observable} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {ListPersonenParameters} from './model/personen/list-personen-parameters';
import {Persoon} from './model/personen/persoon';
import {ListBedrijvenParameters} from './model/bedrijven/list-bedrijven-parameters';
import {Bedrijf} from './model/bedrijven/bedrijf';

@Injectable({
    providedIn: 'root'
})
export class KlantenService {

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {
    }

    private basepath = '/rest/klanten';

    readPersoonOverzicht(bsn: string): Observable<PersoonOverzicht> {
        return this.http.get<PersoonOverzicht>(`${this.basepath}/persoonoverzicht/${bsn}`).pipe(
            catchError(this.handleError)
        );
    }

    listPersonen(listPersonenParameters: ListPersonenParameters): Observable<Persoon[]> {
        return this.http.put<Persoon[]>(`${this.basepath}/personen`, listPersonenParameters).pipe(
            catchError(this.handleError)
        );
    }

    listBedrijven(listBedrijvenParameters: ListBedrijvenParameters): Observable<Bedrijf[]> {
        return this.http.put<Bedrijf[]>(`${this.basepath}/bedrijven`, listBedrijvenParameters).pipe(
            catchError(this.handleError)
        );
    }

    private handleError(err: HttpErrorResponse): Observable<never> {
        return this.foutAfhandelingService.redirect(err);
    }
}
