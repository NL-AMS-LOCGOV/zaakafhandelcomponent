/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {PlanItemDoComponent} from './plan-item-do/plan-item-do.component';
import {PlanItemResolver} from './plan-item.resolver';

const routes: Routes = [
    {
        path: 'plan-items', children: [
            {path: ':id/do', component: PlanItemDoComponent, resolve: {planItem: PlanItemResolver}}
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class PlanItemsRoutingModule {
}
