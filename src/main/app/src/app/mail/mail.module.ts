/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { NgModule } from "@angular/core";

import { SharedModule } from "../shared/shared.module";
import { MailCreateComponent } from "./mail-create/mail-create.component";
import { OntvangstbevestigingComponent } from "./ontvangstbevestiging/ontvangstbevestiging.component";

@NgModule({
  declarations: [MailCreateComponent, OntvangstbevestigingComponent],
  exports: [MailCreateComponent, OntvangstbevestigingComponent],
  imports: [SharedModule],
})
export class MailModule {}
