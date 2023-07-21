/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { NgModule } from "@angular/core";

import { InformatieObjectenRoutingModule } from "./informatie-objecten-routing.module";
import { InformatieObjectViewComponent } from "./informatie-object-view/informatie-object-view.component";
import { SharedModule } from "../shared/shared.module";
import { InformatieObjectEditComponent } from "./informatie-object-edit/informatie-object-edit.component";
import { RouteReuseStrategy } from "@angular/router";
import { RouteReuseStrategyService } from "./route-reuse-strategy.service";
import { InformatieObjectAddComponent } from "./informatie-object-add/informatie-object-add.component";
import { InformatieObjectVerzendenComponent } from "./informatie-object-verzenden/informatie-object-verzenden.component";
import { DocumentIconComponent } from "../shared/document-icon/document-icon.component";
import { InformatieObjectIndicatiesComponent } from "../shared/indicaties/informatie-object-indicaties/informatie-object-indicaties.component";

@NgModule({
  declarations: [
    InformatieObjectViewComponent,
    InformatieObjectEditComponent,
    InformatieObjectAddComponent,
    InformatieObjectVerzendenComponent,
  ],
  exports: [InformatieObjectAddComponent, InformatieObjectVerzendenComponent],
  imports: [
    SharedModule,
    InformatieObjectenRoutingModule,
    DocumentIconComponent,
    InformatieObjectIndicatiesComponent,
  ],
  providers: [
    { provide: RouteReuseStrategy, useClass: RouteReuseStrategyService },
  ],
})
export class InformatieObjectenModule {}
