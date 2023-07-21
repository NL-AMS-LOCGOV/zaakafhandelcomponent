/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { NgModule } from "@angular/core";
import { IdentityService } from "./identity.service";

@NgModule({
  providers: [IdentityService],
})
export class IdentityModule {}
