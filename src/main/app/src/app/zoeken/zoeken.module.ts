/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';

import {SharedModule} from '../shared/shared.module';

import {ZoekComponent} from './zoek/zoek.component';
import {ZaakZoekObjectComponent} from './zoek-object/zaak-zoek-object/zaak-zoek-object.component';
import {TaakZoekObjectComponent} from './zoek-object/taak-zoek-object/taak-zoek-object.component';
import {MultiFacetFilterComponent} from './zoek/filters/multi-facet-filter/multi-facet-filter.component';
import {DateFilterComponent} from './zoek/filters/date-filter/date-filter.component';
import {KlantFilterComponent} from './zoek/filters/klant-filter/klant-filter.component';
import {KlantFilterDialog} from './zoek/filters/klant-filter/klant-filter.dialog';
import {KlantenModule} from '../klanten/klanten.module';
import {DocumentZoekObjectComponent} from './zoek-object/document-zoek-object/document-zoek-object.component';
import {RouterLink} from '@angular/router';
import {ZoekObjectLinkComponent} from './zoek-object/zoek-object-link/zoek-object-link.component';

@NgModule({
    declarations: [
        ZoekComponent,
        MultiFacetFilterComponent,
        ZoekObjectLinkComponent,
        DateFilterComponent,
        ZaakZoekObjectComponent,
        TaakZoekObjectComponent,
        DocumentZoekObjectComponent,
        KlantFilterComponent,
        KlantFilterDialog
    ],
    exports: [
        ZoekComponent,
    ],
    imports: [
        SharedModule,
        KlantenModule,
        RouterLink,
        RouterLink
    ]
})
export class ZoekenModule {
}
