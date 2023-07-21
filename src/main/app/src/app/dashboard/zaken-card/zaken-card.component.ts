/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, OnDestroy } from "@angular/core";
import { ZaakOverzicht } from "../../zaken/model/zaak-overzicht";
import { SignaleringenService } from "../../signaleringen.service";
import { DashboardCardComponent } from "../dashboard-card/dashboard-card.component";
import { WebsocketListener } from "../../core/websocket/model/websocket-listener";
import { WebsocketService } from "../../core/websocket/websocket.service";
import { IdentityService } from "../../identity/identity.service";

@Component({
  selector: "zac-zaken-card",
  templateUrl: "./zaken-card.component.html",
  styleUrls: [
    "../dashboard-card/dashboard-card.component.less",
    "./zaken-card.component.less",
  ],
})
export class ZakenCardComponent
  extends DashboardCardComponent<ZaakOverzicht>
  implements OnDestroy
{
  columns: string[] = [
    "identificatie",
    "startdatum",
    "zaaktype",
    "omschrijving",
    "url",
  ];

  private signaleringListener: WebsocketListener;

  constructor(
    private signaleringenService: SignaleringenService,
    protected identityService: IdentityService,
    protected websocketService: WebsocketService,
  ) {
    super(identityService, websocketService);
  }

  protected onLoad(afterLoad: () => void): void {
    this.signaleringenService
      .listZakenSignalering(this.data.signaleringType)
      .subscribe((zaken) => {
        this.dataSource.data = zaken;
        afterLoad();
      });
  }

  ngOnDestroy(): void {
    this.websocketService.removeListener(this.signaleringListener);
    super.ngOnDestroy();
  }
}
