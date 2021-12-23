/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ParametersComponent} from './parameters/parameters.component';
import {ParameterEditComponent} from './parameter-edit/parameter-edit.component';
import {ZaakafhandelParametersResolver} from './zaakafhandel-parameters-resolver.service';

const routes: Routes = [
    {
        path: 'admin', children: [
            {path: '', redirectTo: 'parameters', pathMatch: 'full'},
            {path: 'parameters', component: ParametersComponent},
            {path: 'parameters/:uuid', component: ParameterEditComponent, resolve: {parameters: ZaakafhandelParametersResolver}}
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class AdminRoutingModule {
}
