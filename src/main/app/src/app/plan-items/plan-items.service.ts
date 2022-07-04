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

    readHumanTaskPlanItem(planItemId: string): Observable<PlanItem> {
        return this.http.get<PlanItem>(`${this.basepath}/humanTaskPlanItem/${planItemId}`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listHumanTaskPlanItems(zaakUuid: string): Observable<PlanItem[]> {
        return this.http.get<PlanItem[]>(`${this.basepath}/zaak/${zaakUuid}/humanTaskPlanItems`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listUserEventListenerPlanItems(zaakUuid: string): Observable<PlanItem[]> {
        return this.http.get<PlanItem[]>(`${this.basepath}/zaak/${zaakUuid}/userEventListenerPlanItems`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    doHumanTaskPlanItem(humanTaskData: HumanTaskData): Observable<void> {
        return this.http.put<void>(`${this.basepath}/doHumanTaskPlanItem`, humanTaskData).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    doUserEventListenerPlanItem(userEventListenerData: UserEventListenerData): Observable<void> {
        return this.http.put<void>(`${this.basepath}/doUserEventListenerPlanItem`, userEventListenerData).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }
}
