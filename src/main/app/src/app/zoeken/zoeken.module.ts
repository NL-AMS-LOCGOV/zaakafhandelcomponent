/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';

import {SharedModule} from '../shared/shared.module';

import {ZoekComponent} from './zoek/zoek.component';
import {ZaakZoekObjectComponent} from './zoek-object/zaak-zoek-object/zaak-zoek-object.component';
import {DateRangeFilterComponent} from './date-range-filter/date-range-filter.component';
import {FacetFilterComponent} from './facet-filter/facet-filter.component';
import {TekstFilterComponent} from './tekst-filter/tekst-filter.component';
import {TaakZoekObjectComponent} from './zoek-object/taak-zoek-object/taak-zoek-object.component';
import {ToggleFilterComponent} from './toggle-filter/toggle-filter.component';
import {MultiFacetFilterComponent} from './multi-facet-filter/multi-facet-filter.component';
import {DateFilterComponent} from './date-filter/date-filter.component';
import {KlantFilterComponent} from './klant-filter/klant-filter.component';
import {KlantFilterDialog} from './klant-filter/klant-filter.dialog';
import {KlantenModule} from '../klanten/klanten.module';

@NgModule({
    declarations: [
        ZoekComponent,
        DateRangeFilterComponent,
        FacetFilterComponent,
        MultiFacetFilterComponent,
        DateFilterComponent,
        TekstFilterComponent,
        ToggleFilterComponent,
        ZaakZoekObjectComponent,
        TaakZoekObjectComponent,
        KlantFilterComponent,
        KlantFilterDialog
    ],
    exports: [
        ZoekComponent,
        DateRangeFilterComponent,
        TekstFilterComponent,
        ToggleFilterComponent,
        FacetFilterComponent,
        MultiFacetFilterComponent,
        DateFilterComponent,
        KlantFilterComponent
    ],
    imports: [
        SharedModule,
        KlantenModule
    ]
})
export class ZoekenModule {
}
