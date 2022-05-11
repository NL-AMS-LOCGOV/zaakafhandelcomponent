/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';

import {SharedModule} from '../shared/shared.module';

import {ZoekComponent} from './zoek/zoek.component';
import {ZaakZoekObjectComponent} from './zoek-object/zaak-zoek-object/zaak-zoek-object.component';

@NgModule({
    declarations: [
        ZoekComponent,
        ZaakZoekObjectComponent
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