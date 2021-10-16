/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';

import {PlanItemsRoutingModule} from './plan-items-routing.module';
import {PlanItemDoComponent} from './plan-item-do/plan-item-do.component';
import {SharedModule} from '../shared/shared.module';

@NgModule({
    declarations: [PlanItemDoComponent],
    imports: [
        SharedModule,
        PlanItemsRoutingModule
    ]
})
export class PlanItemsModule {
}
