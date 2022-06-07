/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';

import {InformatieObjectenRoutingModule} from './informatie-objecten-routing.module';
import {InformatieObjectViewComponent} from './informatie-object-view/informatie-object-view.component';
import {SharedModule} from '../shared/shared.module';
import {InformatieObjectAddComponent} from './informatie-object-add/informatie-object-add.component';
import {InformatieObjectEditComponent} from './informatie-object-edit/informatie-object-edit.component';
import {RouteReuseStrategy} from '@angular/router';
import {RouteReuseStrategyService} from './route-reuse-strategy.service';
import {InformatieObjectCreateComponent} from './informatie-object-create/informatie-object-create.component';

@NgModule({
    declarations: [InformatieObjectViewComponent, InformatieObjectEditComponent, InformatieObjectAddComponent, InformatieObjectCreateComponent],
    exports: [
        InformatieObjectAddComponent,
        InformatieObjectCreateComponent
    ],
    imports: [
        SharedModule,
        InformatieObjectenRoutingModule
    ],
    providers: [
        {provide: RouteReuseStrategy, useClass: RouteReuseStrategyService}
    ]
})
export class InformatieObjectenModule {
}
