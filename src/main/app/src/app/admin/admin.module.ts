/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';

import {SharedModule} from '../shared/shared.module';
import {ParametersComponent} from './parameters/parameters.component';
import {AdminRoutingModule} from './admin-routing.module';
import {ParameterEditComponent} from './parameter-edit/parameter-edit.component';
import {GroepSignaleringenComponent} from './groep-signaleringen/groep-signaleringen.component';

@NgModule({
    declarations: [
        GroepSignaleringenComponent,
        ParametersComponent,
        ParameterEditComponent
    ],
    exports: [],
    imports: [
        SharedModule,
        AdminRoutingModule
    ]
})
export class AdminModule {
}
