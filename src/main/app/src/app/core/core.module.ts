/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { LOCALE_ID, NgModule, Optional, SkipSelf } from "@angular/core";
import { EnsureModuleLoadedOnceGuard } from "./ensure-module-loaded-once.guard";
import { HttpClient, HttpClientModule } from "@angular/common/http";
import { TranslateHttpLoader } from "@ngx-translate/http-loader";
import { TranslateLoader, TranslateModule } from "@ngx-translate/core";
import { UtilService } from "./service/util.service";
import { MAT_DATE_LOCALE } from "@angular/material/core";
import { registerLocaleData } from "@angular/common";
import localeNl from "@angular/common/locales/nl";
import { LoadingComponent } from "./loading/loading.component";
import { SharedModule } from "../shared/shared.module";
import {
  MAT_DIALOG_DEFAULT_OPTIONS,
  MatDialogConfig,
} from "@angular/material/dialog";

registerLocaleData(localeNl, "nl-NL");

// AoT requires an exported function for factories
export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, "./assets/i18n/", ".json");
}

@NgModule({
  declarations: [LoadingComponent],
  imports: [
    HttpClientModule,
    TranslateModule.forRoot({
      defaultLanguage: "nl",
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient],
      },
    }),
    SharedModule,
  ],
  exports: [LoadingComponent],
  providers: [
    UtilService,
    { provide: LOCALE_ID, useValue: "nl-NL" },
    { provide: MAT_DATE_LOCALE, useValue: "nl-NL" },
    {
      provide: MAT_DIALOG_DEFAULT_OPTIONS,
      useValue: {
        ...new MatDialogConfig(),
        minWidth: "500px",
        autoFocus: "dialog",
      },
    },
  ],
})
export class CoreModule extends EnsureModuleLoadedOnceGuard {
  // Ensure that CoreModule is only loaded into AppModule

  // Looks for the module in the parent injector to see if it's already been loaded (only want it loaded once)
  constructor(@Optional() @SkipSelf() parentModule: CoreModule) {
    super(parentModule);
  }
}
