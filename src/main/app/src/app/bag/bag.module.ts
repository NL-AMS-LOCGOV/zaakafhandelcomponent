/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';
import {SharedModule} from '../shared/shared.module';
import {BagZoekComponent} from './zoek/bag-zoek/bag-zoek.component';
import {BAGViewComponent} from './bag-view/bag-view.component';
import {BAGRoutingModule} from './bag-routing.module';

@NgModule({
    declarations: [
        BagZoekComponent,
        BAGViewComponent
    ],
    exports: [
        BagZoekComponent
    ],
    imports: [
        BAGRoutingModule,
        SharedModule
    ]
})
export class BAGModule {}
