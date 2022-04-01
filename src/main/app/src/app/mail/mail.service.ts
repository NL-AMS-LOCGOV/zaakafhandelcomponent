/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {catchError} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {MailObject} from './model/mailobject';

@Injectable({
    providedIn: 'root'
})
export class MailService {

    private basepath = '/rest/mail';

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {
    }

    sendMail(zaakUuid: string, mailObject: MailObject): Observable<number> {
        return this.http.post<number>(`${this.basepath}/send/${zaakUuid}`, mailObject).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    sendAcknowledgeReceipt(zaakUuid: string, mailObject: MailObject): Observable<number> {
        return this.http.post<number>(`${this.basepath}/acknowledge/${zaakUuid}`, mailObject).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }
}
