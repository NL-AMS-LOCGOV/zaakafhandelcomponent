/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { PersoonViewComponent } from "./persoon-view/persoon-view.component";
import { BedrijfViewComponent } from "./bedrijf-view/bedrijf-view.component";
import { BedrijfResolverService } from "./bedrijf-view/bedrijf-resolver.service";
import { PersoonResolverService } from "./persoon-view/persoon-resolver.service";

const routes: Routes = [
  {
    path: "persoon",
    children: [
      {
        path: ":bsn",
        component: PersoonViewComponent,
        resolve: { persoon: PersoonResolverService },
      },
    ],
  },
  {
    path: "bedrijf",
    children: [
      {
        path: ":vesOrRSIN",
        component: BedrijfViewComponent,
        resolve: { bedrijf: BedrijfResolverService },
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class KlantenRoutingModule {}
