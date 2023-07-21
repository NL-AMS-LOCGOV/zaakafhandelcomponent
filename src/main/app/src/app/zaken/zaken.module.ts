/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { NgModule } from "@angular/core";

import { ZakenRoutingModule } from "./zaken-routing.module";
import { ZaakViewComponent } from "./zaak-view/zaak-view.component";
import { ZaakVerkortComponent } from "./zaak-verkort/zaak-verkort.component";
import { SharedModule } from "../shared/shared.module";
import { ZaakCreateComponent } from "./zaak-create/zaak-create.component";
import { ZakenMijnComponent } from "./zaken-mijn/zaken-mijn.component";
import { ZakenAfgehandeldComponent } from "./zaken-afgehandeld/zaken-afgehandeld.component";
import { ZakenVerdelenDialogComponent } from "./zaken-verdelen-dialog/zaken-verdelen-dialog.component";
import { ZakenVrijgevenDialogComponent } from "./zaken-vrijgeven-dialog/zaken-vrijgeven-dialog.component";
import { NotitiesComponent } from "../notities/notities.component";
import { KlantenModule } from "../klanten/klanten.module";
import { LocatieZoekComponent } from "./zoek/locatie-zoek/locatie-zoek.component";
import { InformatieObjectenModule } from "../informatie-objecten/informatie-objecten.module";
import { PlanItemsModule } from "../plan-items/plan-items.module";
import { MailModule } from "../mail/mail.module";
import { ZaakDocumentenComponent } from "./zaak-documenten/zaak-documenten.component";
import { ZakenWerkvoorraadComponent } from "./zaken-werkvoorraad/zaken-werkvoorraad.component";
import { ZoekenModule } from "../zoeken/zoeken.module";
import { ZaakKoppelenDialogComponent } from "./zaak-koppelen/zaak-koppelen-dialog.component";
import { ZaakOntkoppelenDialogComponent } from "./zaak-ontkoppelen/zaak-ontkoppelen-dialog.component";
import { GebruikersvoorkeurenModule } from "../gebruikersvoorkeuren/gebruikersvoorkeuren.module";
import { BAGModule } from "../bag/bag.module";
import { ZaakAfhandelenDialogComponent } from "./zaak-afhandelen-dialog/zaak-afhandelen-dialog.component";
import { BesluitCreateComponent } from "./besluit-create/besluit-create.component";
import { BesluitViewComponent } from "./besluit-view/besluit-view.component";
import { BesluitEditComponent } from "./besluit-edit/besluit-edit.component";
import { IntakeAfrondenDialogComponent } from "./intake-afronden-dialog/intake-afronden-dialog.component";
import { ZaakdataComponent } from "./zaakdata/zaakdata.component";
import { ZaakInitiatorToevoegenComponent } from "./zaak-initiator-toevoegen/zaak-initiator-toevoegen.component";
import { DocumentIconComponent } from "../shared/document-icon/document-icon.component";
import { InformatieObjectIndicatiesComponent } from "../shared/indicaties/informatie-object-indicaties/informatie-object-indicaties.component";
import { ZaakdataFormComponent } from "./zaakdata/zaakdata-form/zaakdata-form.component";

@NgModule({
  declarations: [
    BesluitCreateComponent,
    BesluitEditComponent,
    BesluitViewComponent,
    IntakeAfrondenDialogComponent,
    ZaakViewComponent,
    ZaakVerkortComponent,
    ZaakCreateComponent,
    ZakenWerkvoorraadComponent,
    ZakenMijnComponent,
    ZakenAfgehandeldComponent,
    ZaakAfhandelenDialogComponent,
    ZakenVerdelenDialogComponent,
    ZaakKoppelenDialogComponent,
    ZaakOntkoppelenDialogComponent,
    ZakenVrijgevenDialogComponent,
    ZaakInitiatorToevoegenComponent,
    NotitiesComponent,
    LocatieZoekComponent,
    ZaakDocumentenComponent,
    ZaakdataComponent,
    ZaakdataFormComponent,
  ],
  exports: [ZaakVerkortComponent, ZaakDocumentenComponent],
  imports: [
    SharedModule,
    ZakenRoutingModule,
    KlantenModule,
    InformatieObjectenModule,
    PlanItemsModule,
    MailModule,
    ZoekenModule,
    GebruikersvoorkeurenModule,
    BAGModule,
    DocumentIconComponent,
    InformatieObjectIndicatiesComponent,
  ],
})
export class ZakenModule {}
