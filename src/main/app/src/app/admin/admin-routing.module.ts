/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ParamsComponent} from './params/params.component';
import {ZaakViewComponent} from '../zaken/zaak-view/zaak-view.component';
import {ZaakResolver} from '../zaken/zaak.resolver';
import {ParamViewComponent} from './param-view/param-view.component';
import {ZaakafhandelParametersResolver} from './zaakafhandel-parameters-resolver.service';

const routes: Routes = [
    {
        path: 'admin', children: [
            {path: '', redirectTo: 'params', pathMatch: 'full'},
            {path: 'params', component: ParamsComponent},
            {path: 'params/:uuid', component: ParamViewComponent, resolve: {parameters: ZaakafhandelParametersResolver}}
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class AdminRoutingModule {
}
