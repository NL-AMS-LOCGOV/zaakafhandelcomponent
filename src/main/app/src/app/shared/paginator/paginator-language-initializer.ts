/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Injector } from "@angular/core";
import { TranslateService } from "@ngx-translate/core";
import { LOCATION_INITIALIZED } from "@angular/common";

export function paginatorLanguageInitializerFactory(
  translateService: TranslateService,
  injector: Injector,
) {
  return () =>
    new Promise<any>((resolve: any) => {
      const locationInitialized = injector.get(
        LOCATION_INITIALIZED,
        Promise.resolve(null),
      );
      locationInitialized.then(() => {
        const langToSet = "nl";
        translateService.setDefaultLang("en");
        translateService.use(langToSet).subscribe(() => {
          resolve(null);
        });
      });
    });
}
