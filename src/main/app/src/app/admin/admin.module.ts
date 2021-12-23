/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';

import {SharedModule} from '../shared/shared.module';
import {ParamsComponent} from './params/params.component';
import {AdminRoutingModule} from './admin-routing.module';
import {ParamViewComponent} from './param-view/param-view.component';

@NgModule({
    declarations: [
        ParamsComponent,
        ParamViewComponent
    ],
    exports: [],
    imports: [
        SharedModule,
        AdminRoutingModule
    ]
})
export class AdminModule {
}
