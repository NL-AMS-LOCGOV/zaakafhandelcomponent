/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {InformatieObjectViewComponent} from './informatie-object-view/informatie-object-view.component';
import {InformatieObjectResolver} from './informatie-object.resolver';

const routes: Routes = [
    {
        path: 'informatie-objecten', children: [
            {path: ':uuid', component: InformatieObjectViewComponent, resolve: {informatieObject: InformatieObjectResolver}}
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class InformatieObjectenRoutingModule {
}
