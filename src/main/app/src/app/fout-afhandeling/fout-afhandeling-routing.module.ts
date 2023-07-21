/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { FoutAfhandelingComponent } from "./fout-afhandeling.component";

const routes: Routes = [
  { path: "fout-pagina", component: FoutAfhandelingComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class FoutAfhandelingRoutingModule {}
