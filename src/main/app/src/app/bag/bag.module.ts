/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { NgModule } from "@angular/core";
import { SharedModule } from "../shared/shared.module";
import { BagZoekComponent } from "./zoek/bag-zoek/bag-zoek.component";
import { BAGViewComponent } from "./bag-view/bag-view.component";
import { BAGRoutingModule } from "./bag-routing.module";
import { BagZakenTabelComponent } from "./bag-zaken-tabel/bag-zaken-tabel.component";
import { BagLocatieComponent } from "./bag-locatie/bag-locatie.component";

@NgModule({
  declarations: [
    BagZoekComponent,
    BAGViewComponent,
    BagZakenTabelComponent,
    BagLocatieComponent,
  ],
  exports: [BagZoekComponent],
  imports: [BAGRoutingModule, SharedModule],
})
export class BAGModule {}
