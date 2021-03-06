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
import {ZakenMijnComponent} from './zaken-mijn/zaken-mijn.component';
import {ZakenAfgehandeldComponent} from './zaken-afgehandeld/zaken-afgehandeld.component';
import {ZakenVerdelenDialogComponent} from './zaken-verdelen-dialog/zaken-verdelen-dialog.component';
import {ZakenVrijgevenDialogComponent} from './zaken-vrijgeven-dialog/zaken-vrijgeven-dialog.component';
import {NotitiesComponent} from '../notities/notities.component';
import {KlantenModule} from '../klanten/klanten.module';
import {LocatieZoekComponent} from './zoek/locatie-zoek/locatie-zoek.component';
import {InformatieObjectenModule} from '../informatie-objecten/informatie-objecten.module';
import {PlanItemsModule} from '../plan-items/plan-items.module';
import {MailModule} from '../mail/mail.module';
import {ZaakDocumentenComponent} from './zaak-documenten/zaak-documenten.component';
import {ZakenWerkvoorraadComponent} from './zaken-werkvoorraad/zaken-werkvoorraad.component';
import {ZoekenModule} from '../zoeken/zoeken.module';
import {ZaakKoppelenDialogComponent} from './zaak-koppelen/zaak-koppelen-dialog.component';
import {ZaakOntkoppelenDialogComponent} from './zaak-ontkoppelen/zaak-ontkoppelen-dialog.component';
import {GebruikersvoorkeurenModule} from '../gebruikersvoorkeuren/gebruikersvoorkeuren.module';

@NgModule({
    declarations: [
        ZaakViewComponent,
        ZaakVerkortComponent,
        ZaakCreateComponent,
        ZakenWerkvoorraadComponent,
        ZakenMijnComponent,
        ZakenAfgehandeldComponent,
        ZakenVerdelenDialogComponent,
        ZaakKoppelenDialogComponent,
        ZaakOntkoppelenDialogComponent,
        ZakenVrijgevenDialogComponent,
        NotitiesComponent,
        LocatieZoekComponent,
        ZaakDocumentenComponent
    ],
    exports: [
        ZaakVerkortComponent,
        ZaakDocumentenComponent
    ],
    imports: [
        SharedModule,
        ZakenRoutingModule,
        KlantenModule,
        InformatieObjectenModule,
        PlanItemsModule,
        MailModule,
        ZoekenModule,
        GebruikersvoorkeurenModule
    ]
})
export class ZakenModule {
}
