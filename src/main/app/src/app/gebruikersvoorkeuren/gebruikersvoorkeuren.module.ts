/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { NgModule } from "@angular/core";
import { SharedModule } from "../shared/shared.module";
import { ZoekopdrachtSaveDialogComponent } from "./zoekopdracht-save-dialog/zoekopdracht-save-dialog.component";
import { ZoekopdrachtComponent } from "./zoekopdracht/zoekopdracht.component";

@NgModule({
  declarations: [ZoekopdrachtSaveDialogComponent, ZoekopdrachtComponent],
  exports: [ZoekopdrachtComponent],
  imports: [SharedModule],
})
export class GebruikersvoorkeurenModule {}
