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
import {BetrokkeneZoekComponent} from './zoek/betrokkene/betrokkene-zoek.component';

@NgModule({
    declarations: [
        BedrijfZoekComponent,
        BedrijfsgegevensComponent,
        PersoonZoekComponent,
        PersoonsgegevensComponent,
        BetrokkeneZoekComponent
    ],
    exports: [
        BedrijfZoekComponent,
        BedrijfsgegevensComponent,
        PersoonZoekComponent,
        PersoonsgegevensComponent,
        BetrokkeneZoekComponent
    ],
    imports: [
        SharedModule
    ]
})
export class KlantenModule {
}
