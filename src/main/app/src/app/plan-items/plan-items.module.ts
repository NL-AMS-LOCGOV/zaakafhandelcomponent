/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';

import {PlanItemDoComponent} from './plan-item-do/plan-item-do.component';
import {SharedModule} from '../shared/shared.module';

@NgModule({
    declarations: [PlanItemDoComponent],
    exports: [
        PlanItemDoComponent
    ],
    imports: [
        SharedModule
    ]
})
export class PlanItemsModule {
}
