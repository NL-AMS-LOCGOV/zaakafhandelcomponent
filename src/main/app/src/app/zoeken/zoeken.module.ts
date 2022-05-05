/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';

import {SharedModule} from '../shared/shared.module';

import {ZoekComponent} from './zoek/zoek.component';

@NgModule({
    declarations: [
        ZoekComponent
    ],
    exports: [
        ZoekComponent
    ],
    imports: [
        SharedModule
    ]
})
export class ZoekenModule {
}
