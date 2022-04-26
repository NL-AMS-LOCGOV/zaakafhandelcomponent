/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {Observable} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {PlanItem} from './model/plan-item';
import {UserEventListenerData} from './model/user-event-listener-data';
import {HumanTaskData} from './model/human-task-data';

@Injectable({
    providedIn: 'root'
})
export class PlanItemsService {

    private basepath: string = '/rest/planitems';

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {
    }

    readHumanTask(id: string): Observable<PlanItem> {
        return this.http.get<PlanItem>(`${this.basepath}/${id}`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listPlanItemsForZaak(uuid: string): Observable<PlanItem[]> {
        return this.http.get<PlanItem[]>(`${this.basepath}/zaak/${uuid}`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    doHumanTask(humanTaskData: HumanTaskData): Observable<void> {
        return this.http.put<void>(`${this.basepath}/doHumanTask`, humanTaskData).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    doUserEventListener(userEventListenerData: UserEventListenerData): Observable<void> {
        return this.http.put<void>(`${this.basepath}/doUserEventListener`, userEventListenerData).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }
}
