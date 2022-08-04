/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';

import {SharedModule} from '../shared/shared.module';
import {ParametersComponent} from './parameters/parameters.component';
import {AdminRoutingModule} from './admin-routing.module';
import {ParameterEditComponent} from './parameter-edit/parameter-edit.component';
import {ZoekenModule} from '../zoeken/zoeken.module';

@NgModule({
    declarations: [
        ParametersComponent,
        ParameterEditComponent
    ],
    exports: [],
    imports: [
        SharedModule,
        ZoekenModule,
        AdminRoutingModule
    ]
})
export class AdminModule {
}
