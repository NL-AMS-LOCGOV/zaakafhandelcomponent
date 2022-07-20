/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';

import {SharedModule} from '../shared/shared.module';

import {PersoonZoekComponent} from './zoek/personen/persoon-zoek.component';
import {BedrijfZoekComponent} from './zoek/bedrijven/bedrijf-zoek.component';
import {PersoonsgegevensComponent} from './persoonsgegevens/persoonsgegevens.component';
import {BedrijfsgegevensComponent} from './bedrijfsgegevens/bedrijfsgegevens.component';
import {KlantZoekComponent} from './zoek/klanten/klant-zoek.component';

@NgModule({
    declarations: [
        BedrijfZoekComponent,
        BedrijfsgegevensComponent,
        PersoonZoekComponent,
        PersoonsgegevensComponent,
        KlantZoekComponent
    ],
    exports: [
        BedrijfZoekComponent,
        BedrijfsgegevensComponent,
        PersoonZoekComponent,
        PersoonsgegevensComponent,
        KlantZoekComponent
    ],
    imports: [
        SharedModule
    ]
})
export class KlantenModule {
}
