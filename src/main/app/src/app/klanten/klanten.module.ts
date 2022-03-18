/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';

import {SharedModule} from '../shared/shared.module';

import {PersoonZoekComponent} from './zoek/personen/persoon-zoek.component';
import {BedrijfZoekComponent} from './zoek/bedrijven/bedrijf-zoek.component';

@NgModule({
    declarations: [
        BedrijfZoekComponent,
        PersoonZoekComponent
    ],
    exports: [
        BedrijfZoekComponent,
        PersoonZoekComponent
    ],
    imports: [
        SharedModule
    ]
})
export class KlantenModule {
}
