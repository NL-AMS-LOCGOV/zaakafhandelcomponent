/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';

import {HumanTaskDoComponent} from './human-task-do/human-task-do.component';
import {SharedModule} from '../shared/shared.module';

@NgModule({
    declarations: [HumanTaskDoComponent],
    exports: [
        HumanTaskDoComponent
    ],
    imports: [
        SharedModule
    ]
})
export class PlanItemsModule {
}
