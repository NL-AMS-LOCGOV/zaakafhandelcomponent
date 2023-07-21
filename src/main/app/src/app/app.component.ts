/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { AfterViewInit, Component, OnInit } from "@angular/core";
import { TranslateService } from "@ngx-translate/core";
import { Title } from "@angular/platform-browser";
import { UtilService } from "./core/service/util.service";
import { ZaakKoppelenService } from "./zaken/zaak-koppelen/zaak-koppelen.service";
import { InformatieObjectVerplaatsService } from "./informatie-objecten/informatie-object-verplaats.service";
import { IdentityService } from "./identity/identity.service";
import { SessionStorageUtil } from "./shared/storage/session-storage.util";

@Component({
  selector: "zac-root",
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.less"],
})
export class AppComponent implements OnInit, AfterViewInit {
  initialized = false;

  constructor(
    private translate: TranslateService,
    private titleService: Title,
    private informatieObjectVerplaatsService: InformatieObjectVerplaatsService,
    private zaakKoppelenService: ZaakKoppelenService,
    public utilService: UtilService,
    private identityService: IdentityService,
  ) {}

  ngOnInit(): void {
    this.titleService.setTitle("Zaakafhandelcomponent");
    this.translate.addLangs(["nl", "en"]);
    this.translate.setDefaultLang("nl");
    const browserLanguage = this.translate.getBrowserLang();
    this.translate.use(browserLanguage.match(/nl|en/) ? browserLanguage : "nl");
    SessionStorageUtil.removeItem(IdentityService.LOGGED_IN_USER_KEY);
  }

  ngAfterViewInit(): void {
    if (!this.initialized) {
      this.informatieObjectVerplaatsService.appInit();
      this.zaakKoppelenService.appInit();
      this.initialized = true;
    }
  }
}
