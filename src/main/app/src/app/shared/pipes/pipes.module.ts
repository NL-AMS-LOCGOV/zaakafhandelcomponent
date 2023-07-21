/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { NgModule } from "@angular/core";
import { EmptyPipe } from "./empty.pipe";
import { DatumPipe } from "./datum.pipe";
import { LocationPipe } from "./location.pipe";
import { BestandsomvangPipe } from "./bestandsomvang.pipe";

@NgModule({
  declarations: [BestandsomvangPipe, EmptyPipe, DatumPipe, LocationPipe],
  exports: [BestandsomvangPipe, EmptyPipe, DatumPipe, LocationPipe],
})
export class PipesModule {}
