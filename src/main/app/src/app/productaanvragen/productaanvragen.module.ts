/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { NgModule } from "@angular/core";

import { SharedModule } from "../shared/shared.module";
import { ZoekenModule } from "../zoeken/zoeken.module";
import { GebruikersvoorkeurenModule } from "../gebruikersvoorkeuren/gebruikersvoorkeuren.module";
import { InboxProductaanvragenListComponent } from "./inbox-productaanvragen-list/inbox-productaanvragen-list.component";
import { ProductaanvragenRoutingModule } from "./productaanvragen-routing.module";

@NgModule({
  declarations: [InboxProductaanvragenListComponent],
  imports: [
    SharedModule,
    ProductaanvragenRoutingModule,
    ZoekenModule,
    GebruikersvoorkeurenModule,
  ],
})
export class ProductaanvragenModule {}
