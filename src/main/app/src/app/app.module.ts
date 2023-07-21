/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Injector, NgModule } from "@angular/core";
import { HttpClient, HttpClientModule } from "@angular/common/http";

import { AppRoutingModule } from "./app-routing.module";
import { AppComponent } from "./app.component";
import { CoreModule } from "./core/core.module";
import {
  APP_BASE_HREF,
  LocationStrategy,
  PathLocationStrategy,
} from "@angular/common";
import { SharedModule } from "./shared/shared.module";
import { ZakenModule } from "./zaken/zaken.module";
import { FoutAfhandelingModule } from "./fout-afhandeling/fout-afhandeling.module";
import { DashboardModule } from "./dashboard/dashboard.module";
import { InformatieObjectenModule } from "./informatie-objecten/informatie-objecten.module";
import { PlanItemsModule } from "./plan-items/plan-items.module";
import { TakenModule } from "./taken/taken.module";
import { ToolbarComponent } from "./core/toolbar/toolbar.component";
import { TranslateLoader, TranslateModule } from "@ngx-translate/core";
import { TranslateHttpLoader } from "@ngx-translate/http-loader";
import { AdminModule } from "./admin/admin.module";
import { MailModule } from "./mail/mail.module";
import { DocumentenModule } from "./documenten/documenten.module";
import { ActionBarComponent } from "./core/actionbar/action-bar.component";
import { SignaleringenModule } from "./signaleringen/signaleringen.module";
import { ZoekenModule } from "./zoeken/zoeken.module";
import { GebruikersvoorkeurenModule } from "./gebruikersvoorkeuren/gebruikersvoorkeuren.module";
import { MatIconRegistry } from "@angular/material/icon";
import { ProductaanvragenModule } from "./productaanvragen/productaanvragen.module";

const httpLoaderFactory = (http: HttpClient) =>
  new TranslateHttpLoader(http, "./assets/i18n/", ".json");

@NgModule({
  declarations: [AppComponent, ToolbarComponent, ActionBarComponent],
  imports: [
    HttpClientModule,
    CoreModule,
    SharedModule,
    DashboardModule,
    FoutAfhandelingModule,
    ZakenModule,
    ZoekenModule,
    InformatieObjectenModule,
    DocumentenModule,
    MailModule,
    PlanItemsModule,
    ProductaanvragenModule,
    SignaleringenModule,
    TakenModule,
    AdminModule,
    GebruikersvoorkeurenModule,
    AppRoutingModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: httpLoaderFactory,
        deps: [HttpClient],
      },
    }),
  ],
  exports: [ToolbarComponent],
  providers: [
    { provide: APP_BASE_HREF, useValue: "/" },
    { provide: LocationStrategy, useClass: PathLocationStrategy },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {
  static injector: Injector;

  constructor(injector: Injector, iconRegistry: MatIconRegistry) {
    AppModule.injector = injector;
    iconRegistry.setDefaultFontSetClass("material-symbols-outlined");
  }
}
