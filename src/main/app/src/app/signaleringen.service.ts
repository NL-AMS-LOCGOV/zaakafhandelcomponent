/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {SignaleringType} from './shared/signaleringen/signalering-type';
import {BehaviorSubject, Observable} from 'rxjs';
import {ZaakOverzicht} from './zaken/model/zaak-overzicht';
import {catchError, switchMap} from 'rxjs/operators';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {FoutAfhandelingService} from './fout-afhandeling/fout-afhandeling.service';
import {Taak} from './taken/model/taak';

@Injectable({
    providedIn: 'root'
})
export class SignaleringenService {

    private basepath = '/rest/signaleringen';

    private latestSignaleringSubject: BehaviorSubject<void> = new BehaviorSubject<void>(null);
    latestSignalering$ = this.latestSignaleringSubject.pipe(
        switchMap(() => this.http.get<string>(`${this.basepath}/medewerker/latestsignalering`))
    );

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) { }

    updateSignaleringen(): void {
        this.latestSignaleringSubject.next();
    }

    listZakenSignalering(signaleringType: SignaleringType): Observable<ZaakOverzicht[]> {
        return this.http.get<ZaakOverzicht[]>(`${this.basepath}/zaken/${signaleringType}`).pipe(
            catchError(this.handleError)
        );
    }

    listTakenSignalering(signaleringType: SignaleringType): Observable<Taak[]> {
        return this.http.get<Taak[]>(`${this.basepath}/taken/${signaleringType}`).pipe(
            catchError(this.handleError)
        );
    }

    private handleError(err: HttpErrorResponse): Observable<never> {
        return this.foutAfhandelingService.redirect(err);
    }

}
