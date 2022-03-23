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
import {ZakenWerkvoorraadComponent} from './zaken-werkvoorraad/zaken-werkvoorraad.component';
import {ZakenMijnComponent} from './zaken-mijn/zaken-mijn.component';
import {ZakenAfgehandeldComponent} from './zaken-afgehandeld/zaken-afgehandeld.component';
import {ZakenVerdelenDialogComponent} from './zaken-verdelen-dialog/zaken-verdelen-dialog.component';
import {ZakenVrijgevenDialogComponent} from './zaken-vrijgeven-dialog/zaken-vrijgeven-dialog.component';
import {NotitiesComponent} from '../notities/notities.component';
import {KlantenModule} from '../klanten/klanten.module';

@NgModule({
    declarations: [
        ZaakViewComponent,
        ZaakVerkortComponent,
        ZaakCreateComponent,
        ZakenWerkvoorraadComponent,
        ZakenMijnComponent,
        ZakenAfgehandeldComponent,
        ZakenVerdelenDialogComponent,
        ZakenVrijgevenDialogComponent,
        NotitiesComponent
    ],
    exports: [
        ZaakVerkortComponent
    ],
    imports: [
        SharedModule,
        ZakenRoutingModule,
        KlantenModule
    ]
})
export class ZakenModule {
}
