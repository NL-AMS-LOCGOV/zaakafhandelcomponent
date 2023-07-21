/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component } from "@angular/core";
import { ZaakOverzicht } from "../../zaken/model/zaak-overzicht";
import { DashboardCardComponent } from "../dashboard-card/dashboard-card.component";
import { ZakenService } from "../../zaken/zaken.service";
import { Conditionals } from "../../shared/edit/conditional-fn";
import { IdentityService } from "../../identity/identity.service";
import { WebsocketService } from "../../core/websocket/websocket.service";

@Component({
  selector: "zac-zaak-waarschuwingen-card",
  templateUrl: "./zaak-waarschuwingen-card.component.html",
  styleUrls: [
    "../dashboard-card/dashboard-card.component.less",
    "./zaak-waarschuwingen-card.component.less",
  ],
})
export class ZaakWaarschuwingenCardComponent extends DashboardCardComponent<ZaakOverzicht> {
  columns: string[] = [
    "identificatie",
    "streefdatum",
    "dagenTotStreefdatum",
    "fataledatum",
    "dagenTotFataledatum",
    "url",
  ];

  constructor(
    private zakenService: ZakenService,
    protected identityService: IdentityService,
    protected websocketService: WebsocketService,
  ) {
    super(identityService, websocketService);
  }

  isAfterDate(datum, actual): boolean {
    return Conditionals.isOverschreden(datum, actual);
  }

  protected onLoad(afterLoad: () => void): void {
    this.zakenService.listZaakWaarschuwingen().subscribe((zaken) => {
      this.dataSource.data = zaken;
      afterLoad();
    });
  }
}
