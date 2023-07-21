/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { BAGViewComponent } from "./bag-view/bag-view.component";
import { BAGResolverService } from "./bag-view/bag-resolver.service";

const routes: Routes = [
  {
    path: "bag-objecten",
    children: [
      {
        path: ":type/:id",
        component: BAGViewComponent,
        resolve: { bagObject: BAGResolverService },
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class BAGRoutingModule {}
