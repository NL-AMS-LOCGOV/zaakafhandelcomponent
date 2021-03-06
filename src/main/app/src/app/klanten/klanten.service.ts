/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {Observable} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {ListPersonenParameters} from './model/personen/list-personen-parameters';
import {Persoon} from './model/personen/persoon';
import {ListBedrijvenParameters} from './model/bedrijven/list-bedrijven-parameters';
import {Bedrijf} from './model/bedrijven/bedrijf';
import {Resultaat} from '../shared/model/resultaat';
import {Roltype} from './model/klanten/roltype';

@Injectable({
    providedIn: 'root'
})
export class KlantenService {

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {
    }

    private basepath = '/rest/klanten';

    readPersoon(bsn: string): Observable<Persoon> {
        return this.http.get<Persoon>(`${this.basepath}/persoon/${bsn}`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    readBedrijf(vestigingsnummer: string): Observable<Bedrijf> {
        return this.http.get<Bedrijf>(`${this.basepath}/bedrijf/${vestigingsnummer}`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listPersonen(listPersonenParameters: ListPersonenParameters): Observable<Resultaat<Persoon>> {
        return this.http.put<Resultaat<Persoon>>(`${this.basepath}/personen`, listPersonenParameters).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listBedrijven(listBedrijvenParameters: ListBedrijvenParameters): Observable<Resultaat<Bedrijf>> {
        return this.http.put<Resultaat<Bedrijf>>(`${this.basepath}/bedrijven`, listBedrijvenParameters).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listBetrokkeneRoltypen(zaaktypeUuid: string): Observable<Roltype[]> {
        return this.http.get<Roltype[]>(`${this.basepath}/roltype/${zaaktypeUuid}/betrokkene`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }
}
