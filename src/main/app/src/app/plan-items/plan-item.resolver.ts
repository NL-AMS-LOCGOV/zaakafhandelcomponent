/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs';
import {PlanItemsService} from './plan-items.service';
import {PlanItem} from './model/plan-item';

@Injectable({
    providedIn: 'root'
})
export class PlanItemResolver implements Resolve<PlanItem> {

    constructor(private planItemsService: PlanItemsService) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<PlanItem> {
        const planItemID: string = route.paramMap.get('id');
        return this.planItemsService.readPlanItem(planItemID);
    }
}
