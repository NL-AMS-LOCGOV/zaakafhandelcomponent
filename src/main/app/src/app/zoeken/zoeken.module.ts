/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';

import {SharedModule} from '../shared/shared.module';

import {ZoekComponent} from './zoek/zoek.component';
import {ZaakZoekObjectComponent} from './zoek-object/zaak-zoek-object/zaak-zoek-object.component';
import {DatumFilterComponent} from './datum-filter/datum-filter.component';
import {FacetFilterComponent} from './facet-filter/facet-filter.component';
import {TekstFilterComponent} from './tekst-filter/tekst-filter.component';
import {TaakZoekObjectComponent} from './zoek-object/taak-zoek-object/taak-zoek-object.component';
import {ToggleFilterComponent} from './toggle-filter/toggle-filter.component';

@NgModule({
    declarations: [
        ZoekComponent,
        DatumFilterComponent,
        FacetFilterComponent,
        TekstFilterComponent,
        ToggleFilterComponent,
        ZaakZoekObjectComponent,
        TaakZoekObjectComponent,
    ],
    exports: [
        ZoekComponent,
        DatumFilterComponent,
        TekstFilterComponent,
        ToggleFilterComponent,
        FacetFilterComponent
    ],
    imports: [
        SharedModule
    ]
})
export class ZoekenModule {
}
