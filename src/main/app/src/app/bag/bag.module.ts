/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';
import {SharedModule} from '../shared/shared.module';
import {BagZoekComponent} from './zoek/bag-zoek/bag-zoek.component';

@NgModule({
    declarations: [
        BagZoekComponent
    ],
    exports: [
        BagZoekComponent
    ],
    imports: [
        SharedModule
    ]
})
export class BAGModule {}
