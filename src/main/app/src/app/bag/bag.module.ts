/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';
import {SharedModule} from '../shared/shared.module';
import {BagAdresZoekComponent} from './zoek/bag-adres-zoek/bag-adres-zoek.component';

@NgModule({
    declarations: [
        BagAdresZoekComponent
    ],
    exports: [
        BagAdresZoekComponent
    ],
    imports: [
        SharedModule
    ]
})
export class BAGModule {}
