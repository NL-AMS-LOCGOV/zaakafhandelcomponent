/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, HostListener, Input } from "@angular/core";

import { ZaakZoekObject } from "../../model/zaken/zaak-zoek-object";
import { Router } from "@angular/router";
import { MatSidenav } from "@angular/material/sidenav";
import { ZoekObject } from "../../model/zoek-object";
import { ZoekObjectType } from "../../model/zoek-object-type";
import { IndicatiesLayout } from "../../../shared/indicaties/indicaties.component";
import { TaakZoekObject } from "../../model/taken/taak-zoek-object";
import { DocumentZoekObject } from "../../model/documenten/document-zoek-object";

@Component({
  selector: "zac-zoek-object-link",
  styleUrls: ["./zoek-object-link.component.less"],
  templateUrl: "./zoek-object-link.component.html",
})
export class ZoekObjectLinkComponent {
  @Input() zoekObject: ZoekObject;
  @Input() sideNav: MatSidenav;
  _newtab = false;
  indicatiesLayout = IndicatiesLayout;

  constructor(private router: Router) {}

  @HostListener("document:keydown", ["$event"])
  handleKeydown(event: KeyboardEvent) {
    if (event.key === "Control") {
      this._newtab = true;
    }
  }

  @HostListener("document:keyup", ["$event"])
  handleKeyup(event: KeyboardEvent) {
    if (event.key === "Control") {
      this._newtab = false;
    }
  }

  getLink(): any[] {
    switch (this.zoekObject.type) {
      case ZoekObjectType.ZAAK:
        return ["/zaken/", (this.zoekObject as ZaakZoekObject).identificatie];
      case ZoekObjectType.TAAK:
        return ["/taken/", this.zoekObject.id];
      case ZoekObjectType.DOCUMENT:
        return ["/informatie-objecten/", this.zoekObject.id];
    }
  }

  getName(): string {
    switch (this.zoekObject.type) {
      case ZoekObjectType.ZAAK:
        return (this.zoekObject as ZaakZoekObject).identificatie;
      case ZoekObjectType.TAAK:
        return (this.zoekObject as TaakZoekObject).naam;
      case ZoekObjectType.DOCUMENT:
        return (this.zoekObject as DocumentZoekObject).titel;
    }
  }
}
