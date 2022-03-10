/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';

import {SharedModule} from '../shared/shared.module';
import {OntkoppeldeDocumentenRoutingModule} from './ontkoppelde-documenten-routing.module';
import {OntkoppeldeDocumentenListComponent} from './ontkoppelde-documenten-list/ontkoppelde-documenten-list.component';

@NgModule({
    declarations: [OntkoppeldeDocumentenListComponent],
    imports: [
        SharedModule,
        OntkoppeldeDocumentenRoutingModule
    ]
})
export class OntkoppeldeDocumentenModule {
}
