/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';

import {InformatieObjectenRoutingModule} from './informatie-objecten-routing.module';
import {InformatieObjectViewComponent} from './informatie-object-view/informatie-object-view.component';
import {SharedModule} from '../shared/shared.module';
import {InformatieObjectCreateComponent} from './informatie-object-create/informatie-object-create.component';

@NgModule({
    declarations: [InformatieObjectViewComponent, InformatieObjectCreateComponent],
    exports: [
        InformatieObjectCreateComponent
    ],
    imports: [
        SharedModule,
        InformatieObjectenRoutingModule
    ]
})
export class InformatieObjectenModule {
}
