/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { TabelGegevensResolver } from "../shared/dynamic-table/datasource/tabel-gegevens-resolver.service";
import { Werklijst } from "../gebruikersvoorkeuren/model/werklijst";
import { InboxProductaanvragenListComponent } from "./inbox-productaanvragen-list/inbox-productaanvragen-list.component";

const routes: Routes = [
  {
    path: "productaanvragen",
    children: [
      {
        path: "inbox",
        component: InboxProductaanvragenListComponent,
        resolve: { tabelGegevens: TabelGegevensResolver },
        data: { werklijst: Werklijst.INBOX_PRODUCTAANVRAGEN },
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ProductaanvragenRoutingModule {}
