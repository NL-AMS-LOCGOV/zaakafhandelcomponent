/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';

import {SharedModule} from '../shared/shared.module';

import {PersoonZoekComponent} from './zoek/persoon-zoek.component';

@NgModule({
    declarations: [PersoonZoekComponent],
    exports: [
        PersoonZoekComponent
    ],
    imports: [
        SharedModule
    ]
})
export class PersonenModule {
}
