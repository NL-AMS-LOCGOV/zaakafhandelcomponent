/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { SignaleringenSettingsComponent } from "./signaleringen-settings/signaleringen-settings.component";

const routes: Routes = [
  {
    path: "signaleringen",
    children: [{ path: "settings", component: SignaleringenSettingsComponent }],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SignaleringenRoutingModule {}
