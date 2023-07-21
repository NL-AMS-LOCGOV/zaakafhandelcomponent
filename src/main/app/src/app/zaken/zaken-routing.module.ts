/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { ZaakViewComponent } from "./zaak-view/zaak-view.component";
import { ZaakCreateComponent } from "./zaak-create/zaak-create.component";
import { ZaakIdentificatieResolver } from "./zaak-identificatie-resolver.service";
import { ZakenWerkvoorraadComponent } from "./zaken-werkvoorraad/zaken-werkvoorraad.component";
import { ZakenMijnComponent } from "./zaken-mijn/zaken-mijn.component";
import { ZakenAfgehandeldComponent } from "./zaken-afgehandeld/zaken-afgehandeld.component";
import { TabelGegevensResolver } from "../shared/dynamic-table/datasource/tabel-gegevens-resolver.service";
import { Werklijst } from "../gebruikersvoorkeuren/model/werklijst";

const routes: Routes = [
  {
    path: "zaken",
    children: [
      {
        path: "",
        redirectTo: "werkvoorraad",
        pathMatch: "full",
      },
      {
        path: "mijn",
        component: ZakenMijnComponent,
        resolve: { tabelGegevens: TabelGegevensResolver },
        data: { werklijst: Werklijst.MIJN_ZAKEN },
      },
      {
        path: "werkvoorraad",
        component: ZakenWerkvoorraadComponent,
        resolve: { tabelGegevens: TabelGegevensResolver },
        data: { werklijst: Werklijst.WERKVOORRAAD_ZAKEN },
      },
      { path: "create", component: ZaakCreateComponent },
      {
        path: "afgehandeld",
        component: ZakenAfgehandeldComponent,
        resolve: { tabelGegevens: TabelGegevensResolver },
        data: { werklijst: Werklijst.AFGEHANDELDE_ZAKEN },
      },
      {
        path: ":zaakIdentificatie",
        component: ZaakViewComponent,
        resolve: { zaak: ZaakIdentificatieResolver },
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ZakenRoutingModule {}
