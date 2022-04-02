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

@Injectable({
    providedIn: 'root'
})
export class PlanItemsService {

    private basepath: string = '/rest/planitems';

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {
    }

    readPlanItem(id: string): Observable<PlanItem> {
        return this.http.get<PlanItem>(`${this.basepath}/${id}`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listPlanItemsForZaak(uuid: string): Observable<PlanItem[]> {
        return this.http.get<PlanItem[]>(`${this.basepath}/zaak/${uuid}`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    doPlanItem(planItem: PlanItem, toelichting?: string): Observable<PlanItem> {
        planItem.toelichting = toelichting;
        return this.http.put<PlanItem>(`${this.basepath}/do/${planItem.id}`, planItem).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }
}
