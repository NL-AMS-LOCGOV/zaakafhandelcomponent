/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';

import {ZakenRoutingModule} from './zaken-routing.module';
import {ZaakViewComponent} from './zaak-view/zaak-view.component';
import {ZaakVerkortComponent} from './zaak-verkort/zaak-verkort.component';
import {SharedModule} from '../shared/shared.module';
import {ZaakCreateComponent} from './zaak-create/zaak-create.component';
import {ZaakEditComponent} from './zaak-edit/zaak-edit.component';
import {ZakenWerkvoorraadComponent} from './zaken-werkvoorraad/zaken-werkvoorraad.component';
import {ZakenMijnComponent} from './zaken-mijn/zaken-mijn.component';
import {ZaakToekennenComponent} from './zaak-toekennen/zaak-toekennen.component';
import {StoreModule} from '@ngrx/store';
import {zaakVerkortReducer} from './state/zaak-verkort.reducer';

@NgModule({
    declarations: [
        ZaakViewComponent,
        ZaakVerkortComponent,
        ZaakCreateComponent,
        ZaakEditComponent,
        ZakenWerkvoorraadComponent,
        ZakenMijnComponent,
        ZaakToekennenComponent
    ],
    exports: [
        ZaakVerkortComponent
    ],
    imports: [
        SharedModule,
        ZakenRoutingModule,
        StoreModule.forFeature('zaken', {
            'zaakVerkort': zaakVerkortReducer
        })
    ]
})
export class ZakenModule {
}
