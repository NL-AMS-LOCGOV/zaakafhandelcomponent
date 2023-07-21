/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { NgModule } from "@angular/core";

import { HumanTaskDoComponent } from "./human-task-do/human-task-do.component";
import { SharedModule } from "../shared/shared.module";
import { ProcessTaskDoComponent } from "./process-task-do/process-task-do.component";
import { FormulierenModule } from "../formulieren/formulieren.module";

@NgModule({
  declarations: [HumanTaskDoComponent, ProcessTaskDoComponent],
  exports: [HumanTaskDoComponent, ProcessTaskDoComponent],
  imports: [SharedModule, FormulierenModule],
})
export class PlanItemsModule {}
