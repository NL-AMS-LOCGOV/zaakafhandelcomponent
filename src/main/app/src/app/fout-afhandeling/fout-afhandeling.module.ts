/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { NgModule } from "@angular/core";

import { SharedModule } from "../shared/shared.module";
import { MatExpansionModule } from "@angular/material/expansion";
import { FoutAfhandelingRoutingModule } from "./fout-afhandeling-routing.module";
import { FoutAfhandelingComponent } from "./fout-afhandeling.component";
import { FoutDialogComponent } from "./dialog/fout-dialog.component";

@NgModule({
  declarations: [FoutAfhandelingComponent, FoutDialogComponent],
  exports: [FoutAfhandelingComponent],
  imports: [SharedModule, MatExpansionModule, FoutAfhandelingRoutingModule],
})
export class FoutAfhandelingModule {}
