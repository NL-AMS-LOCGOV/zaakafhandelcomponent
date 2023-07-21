/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component } from "@angular/core";
import { DashboardCardComponent } from "../dashboard-card/dashboard-card.component";
import { ZoekenService } from "../../zoeken/zoeken.service";
import { ZoekParameters } from "../../zoeken/model/zoek-parameters";
import { ZoekObject } from "../../zoeken/model/zoek-object";
import { TakenMijnDatasource } from "../../taken/taken-mijn/taken-mijn-datasource";
import { SorteerVeld } from "../../zoeken/model/sorteer-veld";
import { IdentityService } from "../../identity/identity.service";
import { WebsocketService } from "../../core/websocket/websocket.service";

@Component({
  selector: "zac-taak-zoeken-card",
  templateUrl: "./taak-zoeken-card.component.html",
  styleUrls: [
    "../dashboard-card/dashboard-card.component.less",
    "./taak-zoeken-card.component.less",
  ],
})
export class TaakZoekenCardComponent extends DashboardCardComponent<ZoekObject> {
  columns: string[] = [
    "naam",
    "creatiedatum",
    "zaakIdentificatie",
    "zaaktypeOmschrijving",
    "url",
  ];

  constructor(
    private zoekenService: ZoekenService,
    protected identityService: IdentityService,
    protected websocketService: WebsocketService,
  ) {
    super(identityService, websocketService);
  }

  protected onLoad(afterLoad: () => void): void {
    const zoekParameters: ZoekParameters = TakenMijnDatasource.mijnLopendeTaken(
      new ZoekParameters(),
    );
    zoekParameters.sorteerVeld = SorteerVeld.TAAK_FATALEDATUM;
    zoekParameters.sorteerRichting = "asc";
    this.zoekenService.list(zoekParameters).subscribe((zoekResultaat) => {
      this.dataSource.data = zoekResultaat.resultaten;
      afterLoad();
    });
  }
}
