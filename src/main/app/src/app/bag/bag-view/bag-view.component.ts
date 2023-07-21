/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { UtilService } from "../../core/service/util.service";
import { Component, OnInit } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { BAGObject } from "../model/bagobject";
import { Adres } from "../model/adres";
import { BAGObjecttype } from "../model/bagobjecttype";
import { OpenbareRuimte } from "../model/openbare-ruimte";
import { Woonplaats } from "../model/woonplaats";
import { Pand } from "../model/pand";
import { Nummeraanduiding } from "../model/nummeraanduiding";
import { Geometry } from "../../zaken/model/geometry";

@Component({
  templateUrl: "./bag-view.component.html",
  styleUrls: ["./bag-view.component.less"],
})
export class BAGViewComponent implements OnInit {
  bagObject: BAGObject;
  adres: Adres;
  openbareRuimte: OpenbareRuimte;
  woonplaats: Woonplaats;
  pand: Pand;
  nummeraanduiding: Nummeraanduiding;
  geometrie: Geometry;

  constructor(
    private utilService: UtilService,
    private _route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.utilService.setTitle("bagobjectgegevens");
    this._route.data.subscribe((data) => {
      this.bagObject = data.bagObject;
      switch (this.bagObject.bagObjectType) {
        case BAGObjecttype.ADRES:
          this.adres = this.bagObject as Adres;
          this.geometrie = this.adres.geometry;
          break;
        case BAGObjecttype.ADRESSEERBAAR_OBJECT:
          break; // (Nog) geen zelfstandige entiteit
        case BAGObjecttype.WOONPLAATS:
          this.woonplaats = this.bagObject as Woonplaats;
          break;
        case BAGObjecttype.PAND:
          this.pand = this.bagObject as Pand;
          this.geometrie = this.pand.geometry;
          break;
        case BAGObjecttype.OPENBARE_RUIMTE:
          this.openbareRuimte = this.bagObject as OpenbareRuimte;
          break;
        case BAGObjecttype.NUMMERAANDUIDING:
          this.nummeraanduiding = this.bagObject as Nummeraanduiding;
          break;
      }
    });
  }
}
