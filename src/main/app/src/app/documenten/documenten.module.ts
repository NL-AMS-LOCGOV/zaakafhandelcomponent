/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { NgModule } from "@angular/core";

import { SharedModule } from "../shared/shared.module";
import { DocumentenRoutingModule } from "./documenten-routing.module";
import { OntkoppeldeDocumentenListComponent } from "./ontkoppelde-documenten-list/ontkoppelde-documenten-list.component";
import { InboxDocumentenListComponent } from "./inbox-documenten-list/inbox-documenten-list.component";
import { ZoekenModule } from "../zoeken/zoeken.module";
import { GebruikersvoorkeurenModule } from "../gebruikersvoorkeuren/gebruikersvoorkeuren.module";

@NgModule({
  declarations: [
    OntkoppeldeDocumentenListComponent,
    InboxDocumentenListComponent,
  ],
  imports: [
    SharedModule,
    DocumentenRoutingModule,
    ZoekenModule,
    GebruikersvoorkeurenModule,
  ],
})
export class DocumentenModule {}
